package omnidrive.api.dropbox;

import com.dropbox.core.*;
import omnidrive.api.base.BaseException;
import omnidrive.api.base.BaseAccount;

import java.io.*;


public class DropboxAccount extends BaseAccount {

    private final DbxClient client;

    public DropboxAccount(DbxRequestConfig config, String accessToken) {
        this.client = new DbxClient(config, accessToken);
    }

    @Override
    protected void createRootFolder() throws BaseException {
        try {
            this.client.createFolder(ROOT_FOLDER_PATH);
        } catch (DbxException ex) {
            throw new DropboxException("Failed to create folder.");
        }
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
        String fileId = null;

        try {
            DbxEntry.File file = this.client.uploadFile(getFullPath(name), DbxWriteMode.add(), size, inputStream);
            fileId = file.asFile().name;
        } catch (FileNotFoundException ex) {
            throw new DropboxException("Input file not found.");
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get file metadata.");
        } catch (IOException ex) {
            throw new DropboxException("Failed to upload file.");
        }

        return fileId;
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
    public void removeFolder(String name) throws BaseException {
        try {
            DbxEntry entry = this.client.getMetadata(getFullPath(name));
            if (entry.isFolder()) {
                this.client.delete(getFullPath(name));
            } else {
                throw new DropboxException("Not a folder.");
            }
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }
    }

    @Override
    public long getQuotaUsedSize() throws BaseException {
        long usedQuota;

        try {
            usedQuota = this.client.getAccountInfo().quota.normal;
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
