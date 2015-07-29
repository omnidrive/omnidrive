package omnidrive.api.dropbox;

import com.dropbox.core.*;
import omnidrive.api.base.AccountMetadata;
import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DropboxAccount extends CloudAccount {

    private final DbxClient client;

    public DropboxAccount(DbxRequestConfig config, String accessToken) {
        this.client = new DbxClient(config, accessToken);
    }

    @Override
    protected void fetchMetadata() throws AccountException {
        if (manifestExists()) {
            this.metadata = new AccountMetadata(this.client.getAccessToken(), getManifestId());
        } else {
            this.metadata = new AccountMetadata(this.client.getAccessToken(), null);
        }
    }

    @Override
    protected void createRootFolder() throws AccountException {
        if (!isOmniDriveFolderExists()) {
            try {
                DbxEntry.Folder rootFolder = this.client.createFolder(OMNIDRIVE_ROOT_FOLDER_PATH);
                if (rootFolder == null) {
                    throw new DropboxException("Failed to create folder.");
                } else if (!rootFolder.isFolder()) {
                    throw new DropboxException("Failed to create folder.");
                }
            } catch (DbxException ex2) {
                throw new DropboxException("Failed to create folder.");
            }
        }
    }

    @Override
    protected String getOmniDriveFolderId() throws AccountException {
        if (this.omniDriveFolderId != null) {
            return this.omniDriveFolderId;
        }

        try {
            DbxEntry rootEntry = this.client.getMetadata(OMNIDRIVE_ROOT_FOLDER_PATH);
            if (rootEntry != null) {
                if (rootEntry.isFolder()) {
                    this.omniDriveFolderId = rootEntry.name;
                }
            }
        } catch (DbxException ex1) {
            this.omniDriveFolderId = null;
        }

        return this.omniDriveFolderId;
    }

    @Override
    public String getUsername() {
        String name;

        try {
            name = this.client.getAccountInfo().displayName;
        } catch (DbxException ex) {
            name = null;
        }

        return name;
    }

    @Override
    public String getUserId() {
        String id;

        try {
            id = String.valueOf(this.client.getAccountInfo().userId);
        } catch (DbxException ex) {
            id = null;
        }

        return id;
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) throws AccountException {
        String fileName = null;

        try {
            DbxEntry.File file = this.client.uploadFile(getFullPath(name), DbxWriteMode.add(), size, inputStream);
            if (file != null) {
                fileName = file.asFile().name;
                this.usedSize += file.numBytes;
            }
        } catch (FileNotFoundException ex) {
            throw new DropboxException("Input file not found.");
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get file metadata.");
        } catch (IOException ex) {
            throw new DropboxException("Failed to upload file.");
        }

        return fileName;
    }

    @Override
    public long downloadFile(String name, OutputStream outputStream) throws AccountException {
        long size = 0;

        try {
            DbxEntry.File dbxFile = this.client.getFile(getFullPath(name), null, outputStream);
            if (dbxFile != null) {
                size = dbxFile.numBytes;
            }
        } catch (IOException ex) {
            throw new DropboxException("Failed to download file.");
        } catch (DbxException ex) {
            throw new DropboxException("Failed to access remote path.");
        }

        return size;
    }

    @Override
    public void removeFile(String name) throws AccountException {
        try {
            DbxEntry entry = this.client.getMetadata(getFullPath(name));
            if (entry.isFile()) {
                long fileSize = entry.asFile().numBytes;
                this.client.delete(getFullPath(name));
                this.usedSize -= fileSize;
            } else {
                throw new DropboxException("Not a file.");
            }
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }
    }

    @Override
    public void updateFile(String name, InputStream inputStream, long size) throws AccountException {
        try {
            DbxEntry entry = this.client.getMetadata(getFullPath(name));
            if (entry != null) {
                if (entry.isFile()) {
                    this.usedSize -= entry.asFile().numBytes;
                    DbxEntry.File updatedFile = this.client.uploadFile(entry.asFile().path, DbxWriteMode.update(entry.asFile().rev), size, inputStream);
                    if (updatedFile != null) {
                        this.usedSize += updatedFile.numBytes;
                    } else {
                        throw new DropboxException("Failed to update file");
                    }
                } else {
                    throw new DropboxException("Not a file.");
                }
            } else {
                throw new DropboxException("File does not exist.");
            }
        } catch (IOException ex) {
            throw new DropboxException("Failed to update file: " + ex.getMessage());
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }
    }

    @Override
    public void removeFolder(String name) throws AccountException {
        try {
            DbxEntry entry = this.client.getMetadata(getFullPath(name));
            if (entry != null) {
                if (entry.isFolder()) {
                    this.client.delete(getFullPath(name));
                    this.usedSize = getQuotaUsedSize();
                } else {
                    throw new DropboxException("Not a folder.");
                }
            } else {
                throw new DropboxException("Folder ndoes not exist.");
            }
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }
    }

    @Override
    public long downloadManifest(OutputStream outputStream) throws AccountException {
        long size = 0;

        if (!isOmniDriveFolderExists()) {
            throw new DropboxException("No 'OmniDrive' root folder exists");
        }

        setManifestId(MANIFEST_FILE_NAME);
        size = downloadFile(getManifestId(), outputStream);

        return size;
    }

    @Override
    public void uploadManifest(InputStream inputStream, long size) throws AccountException {
        String manifestFileId = uploadFile(MANIFEST_FILE_NAME, inputStream, size);
        setManifestId(manifestFileId);
    }

    @Override
    public void removeManifest() throws AccountException {
        removeManifest(AccountType.Dropbox);
    }

    @Override
    public boolean manifestExists() throws AccountException {
        boolean exists = false;

        if (hasManifestId()) {
            return true;
        }

        try {
            DbxEntry.WithChildren entryWithChildren = this.client.getMetadataWithChildren(OMNIDRIVE_ROOT_FOLDER_PATH);
            if (entryWithChildren == null) {
                throw new DropboxException("Failed to get 'OmniDrive' folder");
            }
            for (DbxEntry child : entryWithChildren.children) {
                if (child.isFile()) {
                    if (child.name.equals(MANIFEST_FILE_NAME)) {
                        exists = true;
                        setManifestId(child.name);
                        break;
                    }
                }
            }
        } catch (DbxException ex) {
            exists = false;
        }

        return exists;
    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
        long usedQuota;

        try {
            usedQuota = this.client.getAccountInfo().quota.normal + this.client.getAccountInfo().quota.shared;
            this.usedSize = usedQuota;
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get quota used size.");
        }

        return usedQuota;
    }

    @Override
    public long getQuotaTotalSize() throws AccountException {
        long totalQuota;

        try {
            totalQuota = this.client.getAccountInfo().quota.total;
            this.totalSize = totalQuota;
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get quota total size.");
        }

        return totalQuota;
    }
}
