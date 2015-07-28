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
    public String getOmniDriveFolderId() throws BaseException {
        if (this.omniDriveFolderId != null)
            return this.omniDriveFolderId;

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
        } catch (BoxAPIException ex) {
            throw new BoxException(ex.getResponse());
        }

        return fileId;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws BaseException {
        com.box.sdk.BoxFile file = new com.box.sdk.BoxFile(this.user.getAPI(), fileId);
        Info info = file.getInfo();
        file.download(outputStream);

        return info.getSize();
    }

    @Override
    public void removeFile(String fileId) throws BaseException {
        com.box.sdk.BoxFile file = new com.box.sdk.BoxFile(this.user.getAPI(), fileId);

        try {
            file.delete();
        } catch (Exception ex) {
            throw new BoxException(ex.getMessage());
        }
    }

    @Override
    public void removeFolder(String fileId) throws BaseException {
        com.box.sdk.BoxFolder folder = new com.box.sdk.BoxFolder(this.user.getAPI(), fileId);

        try {
            folder.delete(true);
        } catch (Exception ex) {
            throw new BoxException(ex.getMessage());
        }
    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws BaseException {
        com.box.sdk.BoxFile file = new com.box.sdk.BoxFile(this.user.getAPI(), fileId);
        file.uploadVersion(inputStream);
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
                if (itemInfo.getName().equals("manifest")) {
                    String manifestId = itemInfo.getID();
                    com.box.sdk.BoxFile manifestFile = new com.box.sdk.BoxFile(this.user.getAPI(), manifestId);
                    if (manifestFile != null) {
                        size = manifestFile.getInfo().getSize();
                        manifestFile.download(outputStream);
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
    public long getQuotaUsedSize() throws BaseException {
        return this.user.getInfo().getSpaceUsed();
    }

    @Override
    public long getQuotaTotalSize() throws BaseException {
        return this.user.getInfo().getSpaceUsed();
    }
}
