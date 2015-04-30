package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile.Info;

import omnidrive.api.base.BaseException;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseFolder;
import omnidrive.api.base.BaseUser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BoxUser implements BaseUser {

    com.box.sdk.BoxUser user;

    public BoxUser(BoxAPIConnection connection, String id) {
        this.user = new com.box.sdk.BoxUser(connection, id);
    }

    /*******************************************************
     * Interface methods
     *******************************************************/

    public String getName() throws BaseException {
        return this.user.getInfo("name").getName();
    }

    public String getId() throws BaseException {
        return this.user.getID();
    }

    public BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException {
        BoxFile file = null;
        com.box.sdk.BoxFolder rootFolder = com.box.sdk.BoxFolder.getRootFolder(this.user.getAPI());

        try {
            FileInputStream stream = new FileInputStream(localSrcPath);
            Info info = rootFolder.uploadFile(stream, remoteDestPath);
            stream.close();
            file = new BoxFile(this, this.user.getAPI(), info);
        } catch (BoxAPIException ex) {
            throw new BoxException(ex.getResponseCode());
        } catch (IOException ex) {
            throw new BoxException("Failed to upload file");
        }

        return file;
    }

    public FileOutputStream downloadFile(String remoteSrcId, String localDestPath) throws BaseException {
        FileOutputStream stream = null;

        try {
            com.box.sdk.BoxFile file = new com.box.sdk.BoxFile(this.user.getAPI(), remoteSrcId);
            Info info = file.getInfo();
            stream = new FileOutputStream(localDestPath);
            file.download(stream);
            stream.close();
        } catch (FileNotFoundException ex) {
            throw new BoxException("Failed to download file: file not found");
        } catch (IOException ex) {
            throw new BoxException("Failed to download file");
        }

        return stream;
    }

    public BaseFolder createFolder(String remoteParentId, String folderName) throws BaseException {
        com.box.sdk.BoxFolder parentFolder = new com.box.sdk.BoxFolder(this.user.getAPI(), remoteParentId);
        com.box.sdk.BoxFolder.Info childFolderInfo = parentFolder.createFolder(folderName);

        return new BoxFolder(this, this.user.getAPI(), childFolderInfo);
    }

    public BaseFile getFile(String remoteId) throws BaseException {
        com.box.sdk.BoxFile file = new com.box.sdk.BoxFile(this.user.getAPI(), remoteId);
        Info info = file.getInfo();

        return new BoxFile(this, this.user.getAPI(), info);
    }

    public BaseFolder getFolder(String remoteId) throws BaseException {
        com.box.sdk.BoxFolder folder = new com.box.sdk.BoxFolder(this.user.getAPI(), remoteId);
        com.box.sdk.BoxFolder.Info info = folder.getInfo();

        return new BoxFolder(this, this.user.getAPI(), info);
    }

    public BaseFolder getRootFolder() throws BaseException {
        com.box.sdk.BoxFolder rootFolder = com.box.sdk.BoxFolder.getRootFolder(this.user.getAPI());

        return new BoxFolder(this, this.user.getAPI(), rootFolder.getInfo());
    }

    public long getQuotaUsedSize() throws BaseException {
        return this.user.getInfo().getSpaceUsed();
    }

    public long getQuotaTotalSize() throws BaseException {
        return this.user.getInfo().getSpaceUsed();
    }
}
