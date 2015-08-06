package omnidrive.api.base;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Account {

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

    public void initialize() throws AccountException {
        createRootFolder();
        this.usedSize = getQuotaUsedSize();
        this.totalSize = getQuotaTotalSize();
    }

    protected boolean isOmniDriveFolderExists() throws AccountException {
        return getOmniDriveFolderId() != null;
    }

    protected abstract void createRootFolder() throws AccountException;

    protected abstract String getOmniDriveFolderId() throws AccountException;

    public abstract String getUsername() throws AccountException;

    public abstract String getUserId() throws AccountException;

    public abstract String uploadFile(String name, InputStream inputStream, long size) throws AccountException;

    public abstract long downloadFile(String fileId, OutputStream outputStream) throws AccountException;

    public abstract void removeFile(String fileId) throws AccountException;

    public abstract void removeFolder(String fileId) throws AccountException;

    public abstract void updateFile(String fileId, InputStream inputStream, long size) throws AccountException;

    public abstract long downloadManifest(OutputStream outputStream) throws AccountException;

    public abstract void uploadManifest(InputStream inputStream, long size) throws AccountException;

    public abstract void updateManifest(InputStream inputStream, long size) throws AccountException;

    public abstract void removeManifest() throws AccountException;

    protected void removeManifest(AccountType accountType) throws AccountException {
        if (this.manifestFileId == null) {
            throw new AccountException(accountType, "Manifest file not exists.");
        }

        removeFile(this.manifestFileId);
    }

    protected boolean hasManifestId() {
        return this.manifestFileId != null;
    }

    public abstract boolean manifestExists() throws AccountException;

    public abstract long getQuotaUsedSize() throws AccountException;

    public abstract long getQuotaTotalSize() throws AccountException;

    public long getQuotaRemainingSize() throws AccountException {
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
