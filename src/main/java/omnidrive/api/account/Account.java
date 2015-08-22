package omnidrive.api.account;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Account {

    protected static final String MANIFEST_FILE_NAME = "manifest";
    protected static final String OMNIDRIVE_ROOT_FOLDER_NAME = ".omnidrive";
    protected static final String OMNIDRIVE_ROOT_FOLDER_PATH = "/" + OMNIDRIVE_ROOT_FOLDER_NAME;

    protected AccountMetadata metadata;

    protected long totalSize = 0;
    protected long usedSize = 0;

    private AccountType type;

    protected Account(AccountType type, AccountMetadata metadata) {
        this.type = type;
        this.metadata = metadata;
    }

    protected String getFullRootFolderPath() {
        return OMNIDRIVE_ROOT_FOLDER_PATH + "/";
    }

    protected String getFullPath(String name) {
        return getFullRootFolderPath() + name;
    }

    public void initialize() throws AccountException {
        createRootFolder();
        fetchManifestIdIfExists();
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

    public abstract void removeFolder(String folderId) throws AccountException;

    public abstract void updateFile(String fileId, InputStream inputStream, long size) throws AccountException;

    public long downloadManifest(OutputStream outputStream) throws AccountException {
        long size = 0;

        if (hasManifestId()) {
            size = downloadFile(this.metadata.getManifestId(), outputStream);
        }

        return size;
    }

    public void uploadManifest(InputStream inputStream, long size) throws AccountException {
        String fileId = uploadFile(MANIFEST_FILE_NAME, inputStream, size);
        this.metadata.setManifestId(fileId);
    }

    public void updateManifest(InputStream inputStream, long size) throws AccountException {
        if (!hasManifestId()) {
            uploadManifest(inputStream, size);
        } else {
            updateFile(this.metadata.getManifestId(), inputStream, size);
        }
    }

    public void removeManifest() throws AccountException {
        if (hasManifestId()) {
            removeFile(metadata.getManifestId());
        }
    }

    protected boolean hasManifestId() {
        return metadata.getManifestId() != null;
    }

    protected void fetchManifestIdIfExists() throws AccountException {
        manifestExists(); // also set manifestId
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

    public AccountMetadata getMetadata() {
        return this.metadata;
    }

    public AccountType getType() {
        return this.type;
    }
}
