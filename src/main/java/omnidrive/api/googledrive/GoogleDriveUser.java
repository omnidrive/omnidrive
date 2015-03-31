package omnidrive.api.googledrive;

import com.google.api.services.drive.Drive;
import omnidrive.api.base.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GoogleDriveUser implements BaseUser {

    private Drive service;

    public GoogleDriveUser(Drive service) {
        this.service = service;
    }

    public String getName() {
        String name = null;

        try {
            name = service.about().get().execute().getName();
        } catch (IOException ex) {
            name = null;
        }

        return name;
    }

    public String getId() {
        String id = null;

        try {
            id = service.about().get().execute().getPermissionId();
        } catch (IOException ex) {
            id = null;
        }

        return id;
    }

    public BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException {
        return null;
    }

    public BaseFile uploadFile(File inputFile, String remoteDestPath) throws BaseException {
        return null;
    }

    public FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws BaseException {
        return null;
    }

    public FileOutputStream downloadFile(BaseFile file, String localDestPath) throws BaseException {
        return null;
    }

    public BaseFolder createFolder(String remoteDestPath) throws BaseException {
        return null;
    }

    public BaseFile getFile(String remotePath) throws BaseException {
        return null;
    }

    public BaseFolder getFolder(String path) throws BaseException {
        return null;
    }

}
