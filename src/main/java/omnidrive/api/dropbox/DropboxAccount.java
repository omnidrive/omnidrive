package omnidrive.api.dropbox;

import com.dropbox.core.*;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DropboxAccount extends BaseAccount {

    private final DbxClient client;

    public DropboxAccount(DbxRequestConfig config, String accessToken) {
        this.client = new DbxClient(config, accessToken);
    }

    @Override
    protected void createRootFolder() throws BaseException {
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
    public String getOmniDriveFolderId() throws BaseException {
        if (this.omniDriveFolderId != null)
            return this.omniDriveFolderId;

        try {
            DbxEntry rootEntry = this.client.getMetadata(OMNIDRIVE_ROOT_FOLDER_PATH);
            if (rootEntry != null) {
                if (rootEntry.isFolder()) {
                    this.omniDriveFolderId = rootEntry.path;
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
    public String uploadFile(String name, InputStream inputStream, long size) throws BaseException {
        String fileName = null;

        try {
            DbxEntry.File file = this.client.uploadFile(getFullPath(name), DbxWriteMode.add(), size, inputStream);
            if (file != null) {
                fileName = file.asFile().name;
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
    public long downloadFile(String name, OutputStream outputStream) throws BaseException {
        long size = 0;

        try {
            DbxEntry.File dbxFile = this.client.getFile(getFullPath(name), null, outputStream);
            size = dbxFile.numBytes;
        } catch (IOException ex) {
            throw new DropboxException("Failed to download file.");
        } catch (DbxException ex) {
            throw new DropboxException("Failed to access remote path.");
        }

        return size;
    }

    @Override
    public void removeFile(String name) throws BaseException {
        try {
            DbxEntry entry = this.client.getMetadata(getFullPath(name));
            if (entry.isFile()) {
                this.client.delete(getFullPath(name));
            } else {
                throw new DropboxException("Not a file.");
            }
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }
    }

    @Override
    public void updateFile(String name, InputStream inputStream, long size) throws BaseException {
        try {
            DbxEntry entry = this.client.getMetadata(getFullPath(name));
            if (entry != null) {
                if (entry.isFile()) {
                    this.client.uploadFile(entry.asFile().path, DbxWriteMode.update(entry.asFile().rev), size, inputStream);
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
    public void removeFolder(String name) throws BaseException {
        try {
            DbxEntry entry = this.client.getMetadata(getFullPath(name));
            if (entry != null) {
                if (entry.isFolder()) {
                    this.client.delete(getFullPath(name));
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
    public long downloadManifestFile(OutputStream outputStream) throws BaseException {
        long size = 0;

        if (!isOmniDriveFolderExists()) {
            throw new DropboxException("No 'OmniDrive' root folder exists");
        }

        try {
            DbxEntry.File dbxFile = this.client.getFile(getFullPath("manifest"), null, outputStream);
            if (dbxFile != null) {
                size = dbxFile.numBytes;
            }
        } catch (DbxException ex) {
            throw new DropboxException("Failed to download manifest file.");
        } catch (IOException ex) {
            throw new DropboxException("Failed to find manifest file.");
        }

        return size;
    }

    @Override
    public long getQuotaUsedSize() throws BaseException {
        long usedQuota;

        try {
            usedQuota = this.client.getAccountInfo().quota.normal + this.client.getAccountInfo().quota.shared;
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get quota used size.");
        }

        return usedQuota;
    }

    @Override
    public long getQuotaTotalSize() throws BaseException {
        long totalQuota;

        try {
            totalQuota = this.client.getAccountInfo().quota.total;
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get quota total size.");
        }

        return totalQuota;
    }
}
