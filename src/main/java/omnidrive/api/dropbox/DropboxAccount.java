package omnidrive.api.dropbox;

import com.dropbox.core.*;
import omnidrive.api.account.AccountMetadata;
import omnidrive.api.account.Account;
import omnidrive.api.account.AccountException;
import omnidrive.api.account.AccountType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DropboxAccount extends Account {

    private final DbxClient client;

    public DropboxAccount(AccountMetadata metadata, DbxRequestConfig config) {
        super(AccountType.Dropbox, metadata, null);
        this.client = new DbxClient(config, metadata.getAccessToken());
        // dropbox token never changes, no need for auto-refresh
    }

    @Override
    protected void createRootFolder() throws AccountException {
        if (!isOmniDriveFolderExists()) {
            try {
                DbxEntry.Folder rootFolder = this.client.createFolder(OMNIDRIVE_ROOT_FOLDER_PATH);
                if (rootFolder == null) {
                    throw new DropboxException("Failed to create folder.", null);
                } else if (!rootFolder.isFolder()) {
                    throw new DropboxException("Failed to create folder.", null);
                }
            } catch (DbxException ex) {
                throw new DropboxException("Failed to create folder.", ex);
            }
        }
    }

    @Override
    protected String getOmniDriveFolderId() throws AccountException {
        if (this.metadata.getRootFolderId() != null) {
            return this.metadata.getRootFolderId();
        }

        try {
            DbxEntry rootEntry = this.client.getMetadata(OMNIDRIVE_ROOT_FOLDER_PATH);
            if (rootEntry != null) {
                if (rootEntry.isFolder()) {
                    this.metadata.setRootFolderId(rootEntry.name);
                }
            }
        } catch (DbxException ex1) {
            this.metadata.setRootFolderId(null);
        }

        return this.metadata.getRootFolderId();
    }

    @Override
    public void refreshAuthorization(Object object) throws AccountException {
        // dropbox do not revokes the access token, refresh not needed
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
            throw new DropboxException("Input file not found.", ex);
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get file metadata.", ex);
        } catch (IOException ex) {
            throw new DropboxException("Failed to upload file.", ex);
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
            throw new DropboxException("Failed to download file.", ex);
        } catch (DbxException ex) {
            throw new DropboxException("Failed to access remote path.", ex);
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
                throw new DropboxException("Not a file.", null);
            }
        } catch (DbxException ex) {
            throw new DropboxException("Failed to remove file", ex);
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
                        throw new DropboxException("Failed to update file", null);
                    }
                } else {
                    throw new DropboxException("Not a file.", null);
                }
            } else {
                throw new DropboxException("File does not exist.", null);
            }
        } catch (Exception ex) {
            throw new DropboxException("Failed to update file", ex);
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
                    throw new DropboxException("Not a folder.", null);
                }
            } else {
                throw new DropboxException("Folder ndoes not exist.", null);
            }
        } catch (DbxException ex) {
            throw new DropboxException("Failed to remove folder", ex);
        }
    }

    @Override
    public void fetchManifestId() throws AccountException {
        if (manifestExists()) {
            return;
        }

        try {
            DbxEntry.WithChildren entryWithChildren = this.client.getMetadataWithChildren(OMNIDRIVE_ROOT_FOLDER_PATH);
            if (entryWithChildren == null) {
                throw new DropboxException("Failed to get 'OmniDrive' folder", null);
            }
            for (DbxEntry child : entryWithChildren.children) {
                if (child.isFile()) {
                    if (child.name.equals(MANIFEST_FILE_NAME)) {
                        this.metadata.setManifestId(child.name);
                        break;
                    }
                }
            }
        } catch (DbxException ex) {
            throw new DropboxException("Failed to fetch 'OmniDrive' folder", ex);
        }
    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
        long usedQuota;

        try {
            usedQuota = this.client.getAccountInfo().quota.normal + this.client.getAccountInfo().quota.shared;
            this.usedSize = usedQuota;
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get quota used size.", ex);
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
            throw new DropboxException("Failed to get quota total size.", ex);
        }

        return totalQuota;
    }
}
