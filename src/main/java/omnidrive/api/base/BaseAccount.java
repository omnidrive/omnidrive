package omnidrive.api.base;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class BaseAccount {

    protected static final String MANIFEST_FILE_NAME = "manifest";
    protected static final String OMNIDRIVE_ROOT_FOLDER_NAME = ".omnidrive";
    protected static final String OMNIDRIVE_ROOT_FOLDER_PATH = "/" + OMNIDRIVE_ROOT_FOLDER_NAME;

    protected String omniDriveFolderId = null;
    protected String manifestFileId = null;

    protected long totalSize = 0;
    protected long usedSize = 0;

    protected String getFullRootFolderPath() {
        return OMNIDRIVE_ROOT_FOLDER_PATH + "/";
    }

    protected String getFullPath(String name) {
        return getFullRootFolderPath() + name;
    }

    public void initialize() throws BaseException {
        createRootFolder();
        this.usedSize = getQuotaUsedSize();
        this.totalSize = getQuotaTotalSize();
    }

    protected boolean isOmniDriveFolderExists() throws BaseException {
        return getOmniDriveFolderId() != null;
    }

    protected abstract void createRootFolder() throws BaseException;

    protected abstract String getOmniDriveFolderId() throws BaseException;

    public abstract String getUsername() throws BaseException;

    public abstract String getUserId() throws BaseException;

    public abstract String uploadFile(String name, InputStream inputStream, long size) throws BaseException;

    public abstract long downloadFile(String fileId, OutputStream outputStream) throws BaseException;

    public abstract void removeFile(String fileId) throws BaseException;

    public abstract void removeFolder(String fileId) throws BaseException;

    public abstract void updateFile(String fileId, InputStream inputStream, long size) throws BaseException;

    public abstract long downloadManifestFile(OutputStream outputStream) throws BaseException;

    public abstract void uploadManifest(InputStream inputStream, long size) throws BaseException;

    public abstract void updateManifest(InputStream inputStream, long size) throws BaseException;

    public abstract boolean manifestExists() throws BaseException;

    public abstract long getQuotaUsedSize() throws BaseException;

    public abstract long getQuotaTotalSize() throws BaseException;

    public long getQuotaRemainingSize() throws BaseException {
        return getQuotaTotalSize() - getQuotaUsedSize();
    }

    public long getCachedQuotaTotalSize() {
        return this.totalSize;
    }

    public long getCachedQuotaUsedSize() {
        return this.usedSize;
    }

    public long getCachedQuotaRemainingSize() {
        return this.totalSize - this.usedSize;
    }
}
