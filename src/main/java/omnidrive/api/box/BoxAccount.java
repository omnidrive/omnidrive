package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile.Info;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;

import java.io.InputStream;
import java.io.OutputStream;

public class BoxAccount extends BaseAccount {

    com.box.sdk.BoxUser user;

    public BoxAccount(BoxAPIConnection connection) {
        this.user = new com.box.sdk.BoxUser(connection, com.box.sdk.BoxUser.getCurrentUser(connection).getID());
    }

    @Override
    protected void createRootFolder() throws BaseException {
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
    protected String getOmniDriveFolderId() throws BaseException {
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
    public String getUsername() throws BaseException {
        return this.user.getInfo("name").getName();
    }

    @Override
    public String getUserId() throws BaseException {
        return this.user.getID();
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) throws BaseException {
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
    public long downloadFile(String fileId, OutputStream outputStream) throws BaseException {
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
    public void removeFile(String fileId) throws BaseException {
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
    public void removeFolder(String fileId) throws BaseException {
        com.box.sdk.BoxFolder folder = new com.box.sdk.BoxFolder(this.user.getAPI(), fileId);

        try {
            long folderSize = folder.getInfo().getSize();
            folder.delete(true);
            this.usedSize -= folderSize;
        } catch (Exception ex) {
            throw new BoxException(ex.getMessage());
        }
    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws BaseException {
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
    public long downloadManifestFile(OutputStream outputStream) throws BaseException {
        long size = 0;

        if (!isOmniDriveFolderExists()) {
            throw new BoxException("No 'OmniDrive' root folder exists");
        }

        com.box.sdk.BoxFolder rootFolder = new com.box.sdk.BoxFolder(this.user.getAPI(), getOmniDriveFolderId());
        if (rootFolder != null) {
            for (com.box.sdk.BoxItem.Info itemInfo : rootFolder.getChildren()) {
                if (itemInfo.getName().equals(MANIFEST_FILE_NAME)) {
                    String manifestId = itemInfo.getID();
                    com.box.sdk.BoxFile manifestFile = new com.box.sdk.BoxFile(this.user.getAPI(), manifestId);
                    if (manifestFile != null) {
                        size = manifestFile.getInfo().getSize();
                        manifestFile.download(outputStream);
                        this.manifestFileId = manifestId;
                        break;
                    } else {
                        throw new BoxException("Failed to download 'manifest' file");
                    }
                }
            }
        } else {
            throw new BoxException("Failed to find root folder");
        }

        return size;
    }

    @Override
    public void uploadManifest(InputStream inputStream, long size) throws BaseException {
        uploadFile(MANIFEST_FILE_NAME, inputStream, size);
    }

    public void updateManifest(InputStream inputStream, long size) throws BaseException {
        if (this.manifestFileId == null) {
            throw new BoxException("Manifest file id does not exist");
        }

        updateFile(this.manifestFileId, inputStream, size);
    }

    @Override
    public boolean manifestExists() throws BaseException {
        boolean exists = false;

        if (this.manifestFileId != null) {
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
                    break;
                }
            }
        } catch (Exception ex) {
            throw new BoxException("Failed to get 'OmniDrive' folder");
        }

        return exists;
    }

    @Override
    public long getQuotaUsedSize() throws BaseException {
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
    public long getQuotaTotalSize() throws BaseException {
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
