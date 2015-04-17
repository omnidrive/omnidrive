package omnidrive.api.microsoft;

import omnidrive.api.base.BaseException;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseFolder;
import omnidrive.api.base.BaseUser;

import java.io.FileOutputStream;

public class OneDriveUser implements BaseUser {

    private OneDriveAuthProperties properties;

    public OneDriveUser(OneDriveAuthProperties properties) {
        this.properties = properties;
    }

    public final String getName() throws BaseException {
        return properties.getUserId();
    }

    public final String getId() throws BaseException {
        return properties.getUserId();
    }

    public final BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException {
        return null;
    }


    public FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws BaseException {
        return null;
    }


    public BaseFolder createFolder(String remoteParentPath, String folderName) throws BaseException {
        return null;
    }


    public BaseFile getFile(String remotePath) throws BaseException {
        return null;
    }


    public BaseFolder getFolder(String remotePath) throws BaseException {
        return null;
    }


}
