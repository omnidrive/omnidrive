package omnidrive.api.microsoft;

import omnidrive.api.base.Account;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountMetadata;
import omnidrive.api.base.AccountType;
import omnidrive.api.microsoft.lib.core.OneDriveCore;
import omnidrive.api.microsoft.lib.entry.OneDriveChildItem;
import omnidrive.api.microsoft.lib.entry.OneDriveEntryType;
import omnidrive.api.microsoft.lib.entry.OneDriveItem;

import java.io.InputStream;
import java.io.OutputStream;

public class OneDriveAccount extends Account {

    private final OneDriveCore core;

    public OneDriveAccount(OneDriveCore core) {
        super(AccountType.OneDrive);
        this.core = core;
    }

    @Override
    protected void fetchMetadata() throws AccountException {
        if (manifestExists()) {
            this.metadata = new AccountMetadata(this.core.getOauth().getAccessToken(), getManifestId());
        } else {
            this.metadata = new AccountMetadata(this.core.getOauth().getAccessToken(), null);
        }
    }

    @Override
    protected void createRootFolder() throws AccountException {
        if (!isOmniDriveFolderExists()) {
            try {
                this.core.createFolderItem(OMNIDRIVE_ROOT_FOLDER_NAME);
            } catch (Exception ex) {
                throw new OneDriveException("Failed to create 'OmniDrive' folder");
            }
        }
    }

    @Override
    protected String getOmniDriveFolderId() throws AccountException {
        if (this.omniDriveFolderId != null) {
            return this.omniDriveFolderId;
        }

        try {
            OneDriveItem item = this.core.getRootItem();
            for (OneDriveChildItem childItem : item.getChildren()) {
                if (childItem.getType() == OneDriveEntryType.Folder) {
                    if (childItem.getName().equals(OMNIDRIVE_ROOT_FOLDER_NAME)) {
                        this.omniDriveFolderId = childItem.getId();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            throw new OneDriveException("Failed to get root item");
        }

        return this.omniDriveFolderId;
    }

    @Override
    public String getUsername() throws AccountException {
        String name = null;

        try {
            name = this.core.getOwner().getName();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to get username");
        }

        return name;
    }

    @Override
    public String getUserId() throws AccountException {
        String id = null;

        try {
            id = this.core.getOwner().getId();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to get user id");
        }

        return id;
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) throws AccountException {
        String fileId = null;

        try {
            fileId = this.core.uploadItem(getOmniDriveFolderId(), name, inputStream);
            this.usedSize += size;
        } catch (Exception ex) {
            throw new OneDriveException("Failed to upload file");
        }

        return fileId;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws AccountException {
        long size = 0;

        try {
            size = this.core.downloadItem(fileId, outputStream);
        } catch (Exception ex) {
            throw new OneDriveException("Failed to download file");
        }

        return size;
    }

    @Override
    public void removeFile(String fileId) throws AccountException {
        try {
            OneDriveItem file = this.core.getItemById(fileId, false);
            this.core.deleteItem(fileId);
            this.usedSize -= file.getSize();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to remove item");
        }
    }

    @Override
    public void removeFolder(String folderId) throws AccountException {
        removeFile(folderId);
    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws AccountException {
        try {
            OneDriveItem before = this.core.getItemById(fileId, false);
            this.usedSize -= before.getSize();
            this.core.updateItem(fileId, inputStream);
            this.usedSize += size;
        } catch (Exception ex) {
            throw new OneDriveException("Failed to remove item");
        }
    }

    @Override
    public long downloadManifest(OutputStream outputStream) throws AccountException {
        long size = 0;

        if (manifestExists()) {
            size = downloadFile(getManifestId(), outputStream);
        }

        return size;
    }

    @Override
    public void uploadManifest(InputStream inputStream, long size) throws AccountException {
        uploadFile(MANIFEST_FILE_NAME, inputStream, size);
    }

    @Override
    public void removeManifest() throws AccountException {
        if (manifestExists()) {
            removeFile(getManifestId());
        }
    }

    @Override
    public boolean manifestExists() throws AccountException {
        boolean exists = false;

        try {
            OneDriveItem item = this.core.getItemByPath(getFullPath(MANIFEST_FILE_NAME), false);
            if (item != null) {
                setManifestId(item.getId());
                exists = true;
            }
        } catch (Exception ex) {
            exists = false;
        }

        return exists;
    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
        try {
            this.usedSize = this.core.getQuota().getUsed();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to upload file");
        }

        return this.usedSize;
    }

    @Override
    public long getQuotaTotalSize() throws AccountException {
        try {
            this.totalSize = this.core.getQuota().getTotal();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to upload file");
        }

        return this.totalSize;
    }
}
