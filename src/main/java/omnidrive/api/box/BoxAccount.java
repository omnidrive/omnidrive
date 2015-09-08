package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIConnectionListener;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile.Info;
import omnidrive.api.account.*;

import java.io.InputStream;
import java.io.OutputStream;

public class BoxAccount extends Account implements BoxAPIConnectionListener {

    com.box.sdk.BoxUser user;

    public BoxAccount(AccountMetadata metadata, BoxAPIConnection connection) {
        this(metadata, connection, null);
    }

    public BoxAccount(AccountMetadata metadata, BoxAPIConnection connection, RefreshedAccountObserver observer) {
        super(AccountType.Box, metadata, observer);
        connection.addListener(this);
        this.user = new com.box.sdk.BoxUser(connection, com.box.sdk.BoxUser.getCurrentUser(connection).getID());
    }

    @Override
    protected void createRootFolder() throws AccountException {
        if (!isOmniDriveFolderExists()) {
            try {
                com.box.sdk.BoxFolder boxRootFolder = com.box.sdk.BoxFolder.getRootFolder(this.user.getAPI());
                if (boxRootFolder != null) {
                    com.box.sdk.BoxFolder.Info folderInfo = boxRootFolder.createFolder(OMNIDRIVE_ROOT_FOLDER_NAME);
                    if (folderInfo == null) {
                        throw new BoxException("Failed to create root folder", null);
                    }
                    this.metadata.setRootFolderId(folderInfo.getID());
                } else {
                    throw new BoxException("Failed to find root folder.", null);
                }
            } catch (Exception ex) {
                throw new BoxException("Failed to create root folder", ex);
            }
        }
    }

    @Override
    protected String getOmniDriveFolderId() throws AccountException {
        if (this.metadata.getRootFolderId() != null) {
            return this.metadata.getRootFolderId();
        }

        com.box.sdk.BoxFolder rootFolder = com.box.sdk.BoxFolder.getRootFolder(this.user.getAPI());

        try {
            if (rootFolder != null) {
                for (com.box.sdk.BoxItem.Info itemInfo : rootFolder.getChildren()) {
                    if (itemInfo.getName().equals(OMNIDRIVE_ROOT_FOLDER_NAME)) {
                        this.metadata.setRootFolderId(itemInfo.getID());
                        break;
                    }
                }
            } else {
                throw new BoxException("Failed to find root folder", null);
            }
        } catch (Exception ex) {
            throw new BoxException("Error while trying to find root folder", ex);
        }

        return this.metadata.getRootFolderId();
    }

    @Override
    public void removeOmniDriveFolder() throws AccountException {
        try {
            com.box.sdk.BoxFolder rootFolder = new com.box.sdk.BoxFolder(this.user.getAPI(), this.metadata.getRootFolderId());
            rootFolder.delete(true);
        } catch (Exception ex) {
            throw new BoxException("Failed to remove 'OmniDrive' root folder.", ex);
        }
    }

    public void refreshAuthorization() throws AccountException {
        refreshAuthorization(null);
    }

    @Override
    public void refreshAuthorization(Object object) throws AccountException {
        try {
            this.user.getAPI().refresh();
            this.metadata.setAccessToken(this.user.getAPI().getAccessToken());
            this.metadata.setRefreshToken(this.user.getAPI().getRefreshToken());
            notifyRefreshed();
        } catch (Exception ex) {
            throw new BoxException("Failed to refresh account", ex);
        }
    }

    @Override
    public String getUsername() throws AccountException {
        return this.user.getInfo("name").getName();
    }

    @Override
    public String getUserId() throws AccountException {
        return this.user.getID();
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) throws AccountException {
        String fileId = null;
        com.box.sdk.BoxFolder rootFolder = new com.box.sdk.BoxFolder(this.user.getAPI(), getOmniDriveFolderId());

        try {
            Info info = rootFolder.uploadFile(inputStream, name);
            fileId = info.getID();
            this.usedSize += info.getSize();
        } catch (BoxAPIException ex) {
            throw new BoxException("Failed to upload file.", ex);
        }

        return fileId;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws AccountException {
        long size = 0;

        try {
            com.box.sdk.BoxFile file = new com.box.sdk.BoxFile(this.user.getAPI(), fileId);
            if (file != null) {
                Info info = file.getInfo();
                file.download(outputStream);
                size = info.getSize();
            }
        } catch (Exception ex) {
            throw new BoxException("Failed to download file.", ex);
        }

        return size;
    }

    @Override
    public void removeFile(String fileId) throws AccountException {
        com.box.sdk.BoxFile file = new com.box.sdk.BoxFile(this.user.getAPI(), fileId);

        try {
            long fileSize = file.getInfo().getSize();
            file.delete();
            this.usedSize -= fileSize;
        } catch (Exception ex) {
            throw new BoxException("Failed to delete file", ex);
        }
    }

    @Override
    public void removeFolder(String folderId) throws AccountException {
        com.box.sdk.BoxFolder folder = new com.box.sdk.BoxFolder(this.user.getAPI(), folderId);

        try {
            long folderSize = folder.getInfo().getSize();
            folder.delete(true);
            this.usedSize -= folderSize;
        } catch (Exception ex) {
            throw new BoxException("Failed to delete folder", ex);
        }
    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws AccountException {
        try {
            com.box.sdk.BoxFile file = new com.box.sdk.BoxFile(this.user.getAPI(), fileId);
            this.usedSize -= file.getInfo().getSize();
            file.uploadVersion(inputStream);
            this.usedSize += size;
        } catch (Exception ex) {
            throw new BoxException("Failed to update file.", ex);
        }
    }

    @Override
    public void fetchManifestId() throws AccountException {
        if (manifestExists()) {
            return;
        }

        try {
            com.box.sdk.BoxFolder omniDriveFolder = new com.box.sdk.BoxFolder(this.user.getAPI(), getOmniDriveFolderId());
            if (omniDriveFolder == null) {
                throw new BoxException("Failed to get 'OmniDrive' folder", null);
            }

            for (com.box.sdk.BoxItem.Info itemInfo : omniDriveFolder.getChildren()) {
                if (itemInfo.getName().equals(MANIFEST_FILE_NAME)) {
                    this.metadata.setManifestId(itemInfo.getID());
                    break;
                }
            }
        } catch (Exception ex) {
            throw new BoxException("Failed to fetch 'OmniDrive' folder", ex);
        }
    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
        long usedQuota = 0;

        try {
            usedQuota = this.user.getInfo().getSpaceUsed();
            this.usedSize = usedQuota;
        } catch (Exception ex) {
            throw new BoxException("Failed to get quota used size.", ex);
        }

        return usedQuota;
    }

    @Override
    public long getQuotaTotalSize() throws AccountException {
        long totalQuota = 0;

        try {
            totalQuota = this.user.getInfo().getSpaceAmount();
            this.totalSize = totalQuota;
        } catch (Exception ex) {
            throw new BoxException("Failed to get quota total size.", ex);
        }

        return totalQuota;
    }

    @Override
    public void onRefresh(BoxAPIConnection boxAPIConnection) {
        System.out.println("Box: refresh token");
        if (boxAPIConnection.getAccessToken() != null) {
            this.metadata.setAccessToken(boxAPIConnection.getAccessToken());
        }
        if (boxAPIConnection.getRefreshToken() != null) {
            this.metadata.setRefreshToken(boxAPIConnection.getRefreshToken());
        }
        notifyRefreshed();
    }

    @Override
    public void onError(BoxAPIConnection boxAPIConnection, BoxAPIException e) {
        System.out.println("Box error: " + e.getResponse());
    }
}
