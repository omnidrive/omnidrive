package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile.Info;
import omnidrive.api.account.Account;
import omnidrive.api.account.AccountException;
import omnidrive.api.account.AccountMetadata;
import omnidrive.api.account.AccountType;

import java.io.InputStream;
import java.io.OutputStream;

public class BoxAccount extends Account {

    com.box.sdk.BoxUser user;

    public BoxAccount(AccountMetadata metadata, BoxAPIConnection connection) {
        super(AccountType.Box, metadata);
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
                        throw new BoxException("Failed to create root folder");
                    }
                } else {
                    throw new BoxException("Failed to find root folder.");
                }
            } catch (Exception ex) {
                throw new BoxException(ex.getMessage());
            }
        }
    }

    @Override
    protected String getOmniDriveFolderId() throws AccountException {
        if (this.omniDriveFolderId != null) {
            return this.omniDriveFolderId;
        }

        com.box.sdk.BoxFolder rootFolder = com.box.sdk.BoxFolder.getRootFolder(this.user.getAPI());

        try {
            if (rootFolder != null) {
                for (com.box.sdk.BoxItem.Info itemInfo : rootFolder.getChildren()) {
                    if (itemInfo.getName().equals(OMNIDRIVE_ROOT_FOLDER_NAME)) {
                        this.omniDriveFolderId = itemInfo.getID();
                        break;
                    }
                }
            } else {
                throw new BoxException("Failed to find root folder");
            }
        } catch (Exception ex) {
            throw new BoxException(ex.getMessage());
        }

        return this.omniDriveFolderId;
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
            throw new BoxException(ex.getResponse());
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
            throw new BoxException("Failed to download file.");
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
            throw new BoxException(ex.getMessage());
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
            throw new BoxException(ex.getMessage());
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
            throw new BoxException("Failed to update file.");
        }
    }

    @Override
    public boolean manifestExists() throws AccountException {
        boolean exists = false;

        if (hasManifestId()) {
            return true;
        }

        try {
            com.box.sdk.BoxFolder omniDriveFolder = new com.box.sdk.BoxFolder(this.user.getAPI(), getOmniDriveFolderId());
            if (omniDriveFolder == null) {
                throw new BoxException("Failed to fetch 'OmniDrive' folder");
            }

            for (com.box.sdk.BoxItem.Info itemInfo : omniDriveFolder.getChildren()) {
                if (itemInfo.getName().equals(MANIFEST_FILE_NAME)) {
                    exists = true;
                    this.manifestId = itemInfo.getID();
                    break;
                }
            }
        } catch (Exception ex) {
            throw new BoxException("Failed to get 'OmniDrive' folder");
        }

        return exists;
    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
        long usedQuota = 0;

        try {
            usedQuota = this.user.getInfo().getSpaceUsed();
            this.usedSize = usedQuota;
        } catch (Exception ex) {
            throw new BoxException("Failed to get quota used size.");
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
            throw new BoxException("Failed to get quota total size.");
        }

        return totalQuota;
    }
}
