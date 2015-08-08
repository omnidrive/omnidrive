package omnidrive.api.base;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CloudAccount {

    protected static final String MANIFEST_FILE_NAME = "manifest";
    protected static final String OMNIDRIVE_ROOT_FOLDER_NAME = ".omnidrive";
    protected static final String OMNIDRIVE_ROOT_FOLDER_PATH = "/" + OMNIDRIVE_ROOT_FOLDER_NAME;

    protected AccountMetadata metadata;

    protected String omniDriveFolderId = null;

    protected long totalSize = 0;
    protected long usedSize = 0;

    private AccountType type;

    protected CloudAccount(AccountType type) {
        this.type = type;
        this.metadata = new AccountMetadata();
    }

    protected String getFullRootFolderPath() {
        return OMNIDRIVE_ROOT_FOLDER_PATH + "/";
    }

    protected String getFullPath(String name) {
        return getFullRootFolderPath() + name;
    }

    public void initialize() throws AccountException {
        createRootFolder();
        fetchMetadata();
        this.usedSize = getQuotaUsedSize();
        this.totalSize = getQuotaTotalSize();
    }

    protected boolean isOmniDriveFolderExists() throws AccountException {
        return getOmniDriveFolderId() != null;
    }

    protected abstract void fetchMetadata() throws AccountException;

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

    public abstract void removeManifest() throws AccountException;

    public void updateManifest(InputStream inputStream, long size) throws AccountException {
        if (!hasManifestId()) {
            uploadManifest(inputStream, size);
        } else {
            updateFile(getManifestId(), inputStream, size);
        }
    }

    protected void removeManifest(AccountType accountType) throws AccountException {
        if (!hasManifestId()) {
            throw new AccountException(accountType, "Manifest file not exists.");
        }

        removeFile(getManifestId());
    }

    protected boolean hasManifestId() {
        return getManifestId() != null;
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

    protected String getManifestId() {
        return this.metadata.getManifestId();
    }

    public AccountMetadata getMetadata() {
        return this.metadata;
    }

    public void setManifestId(String manifestId) {
        if (this.metadata != null) {
            this.metadata.setManifestId(manifestId);
        } else {
            this.metadata = new AccountMetadata();
            this.metadata.setManifestId(manifestId);
        }
    }

    public AccountType getType() {
        return this.type;
    }
}
