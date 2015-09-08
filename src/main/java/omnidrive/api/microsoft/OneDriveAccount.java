package omnidrive.api.microsoft;

import omnidrive.api.account.*;
import omnidrive.api.microsoft.lib.auth.OneDriveOAuth;
import omnidrive.api.microsoft.lib.core.OneDriveCore;
import omnidrive.api.microsoft.lib.core.OneDriveNameConflict;
import omnidrive.api.microsoft.lib.core.OneDriveRefreshListener;
import omnidrive.api.microsoft.lib.entry.OneDriveChildItem;
import omnidrive.api.microsoft.lib.entry.OneDriveEntryType;
import omnidrive.api.microsoft.lib.entry.OneDriveItem;

import java.io.InputStream;
import java.io.OutputStream;

public class OneDriveAccount extends Account implements OneDriveRefreshListener {

    private final OneDriveCore core;


    public OneDriveAccount(AccountMetadata metadata, OneDriveCore core) {
        this(metadata, core, null);
    }

    public OneDriveAccount(AccountMetadata metadata, OneDriveCore core, RefreshedAccountObserver observer) {
        super(AccountType.OneDrive, metadata, observer);
        this.core = core;
        this.core.addListener(this);
    }

    @Override
    protected void createRootFolder() throws AccountException {
        if (!isOmniDriveFolderExists()) {
            try {
                String folderId = this.core.createFolderItem(OMNIDRIVE_ROOT_FOLDER_NAME, OneDriveNameConflict.Fail);
                this.metadata.setRootFolderId(folderId);
            } catch (Exception ex) {
                throw new OneDriveException("Failed to create 'OmniDrive' folder", ex);
            }
        }
    }

    @Override
    protected String getOmniDriveFolderId() throws AccountException {
        if (this.metadata.getRootFolderId() != null) {
            return this.metadata.getRootFolderId();
        }

        try {
            OneDriveItem item = this.core.getRootItem();
            for (OneDriveChildItem childItem : item.getChildren()) {
                if (childItem.getType() == OneDriveEntryType.Folder) {
                    if (childItem.getName().equals(OMNIDRIVE_ROOT_FOLDER_NAME)) {
                        this.metadata.setRootFolderId(childItem.getId());
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            throw new OneDriveException("Failed to get root item", ex);
        }

        return this.metadata.getRootFolderId();
    }

    @Override
    public void removeOmniDriveFolder() throws AccountException {
        try {
            this.core.deleteItem(this.metadata.getRootFolderId());
        } catch (Exception ex) {
            throw new OneDriveException("Failed to remove 'OmniDrive' root folder.", ex);
        }
    }

    public void refreshAuthorization() throws AccountException {
        refreshAuthorization(null);
    }

    @Override
    public void refreshAuthorization(Object object) throws AccountException {
        try {
            this.core.refreshTheAccessToken();
            this.metadata.setRefreshToken(this.core.getOauth().getRefreshToken());
            this.metadata.setAccessToken(this.core.getOauth().getAccessToken());
            notifyRefreshed();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to refresh account.", ex);
        }
    }

    @Override
    public String getUsername() throws AccountException {
        String name = null;

        try {
            name = this.core.getOwner().getName();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to get username", ex);
        }

        return name;
    }

    @Override
    public String getUserId() throws AccountException {
        String id = null;

        try {
            id = this.core.getOwner().getId();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to get user id", ex);
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
            throw new OneDriveException("Failed to upload file", ex);
        }

        return fileId;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws AccountException {
        long size = 0;

        try {
            size = this.core.downloadItem(fileId, outputStream);
        } catch (Exception ex) {
            throw new OneDriveException("Failed to download file", ex);
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
            throw new OneDriveException("Failed to remove item", ex);
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
            this.core.updateItem(fileId, inputStream, OneDriveNameConflict.Replace);
            this.usedSize += size;
        } catch (Exception ex) {
            throw new OneDriveException("Failed to remove item", ex);
        }
    }

    @Override
    public void fetchManifestId() throws AccountException {
        if (manifestExists()) {
            return;
        }

        try {
            OneDriveItem item = this.core.getItemByPath(getFullPath(MANIFEST_FILE_NAME), false);
            if (item != null) {
                if (!item.isDeleted()) {
                    this.metadata.setManifestId(item.getId());
                }
            }
        } catch (Exception ex) {
            this.metadata.setManifestId(null);
        }
    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
        try {
            this.usedSize = this.core.getQuota().getUsed();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to upload file", ex);
        }

        return this.usedSize;
    }

    @Override
    public long getQuotaTotalSize() throws AccountException {
        try {
            this.totalSize = this.core.getQuota().getTotal();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to upload file", ex);
        }

        return this.totalSize;
    }

    @Override
    public void onRefresh(OneDriveCore core, OneDriveOAuth newOAuth) {
        System.out.println("OneDrive: refresh token");
        if (newOAuth.getAccessToken() != null) {
            this.metadata.setAccessToken(newOAuth.getAccessToken());
        }
        if (newOAuth.getRefreshToken() != null) {
            this.metadata.setRefreshToken(newOAuth.getRefreshToken());
        }
        notifyRefreshed();
    }
}
