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

    public BoxAccount(BoxAPIConnection connection, String id) {
        this.user = new com.box.sdk.BoxUser(connection, id);
    }

    @Override
    protected void createRootFolder() throws BaseException {
        com.box.sdk.BoxFolder parentFolder = new com.box.sdk.BoxFolder(this.user.getAPI(), "/");
        parentFolder.createFolder(ROOT_FOLDER_NAME);
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
        com.box.sdk.BoxFolder rootFolder = com.box.sdk.BoxFolder.getRootFolder(this.user.getAPI());

        try {
            Info info = rootFolder.uploadFile(inputStream, getFullPath(name));
            fileId = info.getID();
        } catch (BoxAPIException ex) {
            throw new BoxException(ex.getResponseCode());
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
    public long getQuotaUsedSize() throws BaseException {
        return this.user.getInfo().getSpaceUsed();
    }

    @Override
    public long getQuotaTotalSize() throws BaseException {
        return this.user.getInfo().getSpaceUsed();
    }
}
