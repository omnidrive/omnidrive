package omnidrive.api.google;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import omnidrive.api.base.*;

import java.io.FileOutputStream;
import java.io.IOException;

import com.google.api.services.drive.model.File;

public class GoogleDriveUser implements BaseUser {

    private Drive service;

    public GoogleDriveUser(Drive service) {
        this.service = service;
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public String getName() {
        String name = null;

        try {
            name = this.service.about().get().execute().getName();
        } catch (IOException ex) {
            name = null;
        }

        return name;
    }

    public String getId() {
        String id = null;

        try {
            id = this.service.about().get().execute().getPermissionId();
        } catch (IOException ex) {
            id = null;
        }

        return id;
    }

    public BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException {
        //Insert a file
        File body = new File();
        body.setTitle(remoteDestPath);

        java.io.File fileContent = new java.io.File(localSrcPath);
        FileContent mediaContent = new FileContent(null, fileContent);

        File uploadedFile = null;
        try {
            uploadedFile = this.service.files().insert(body, mediaContent).execute();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to insert file");
        }

        return new GoogleDriveFile(uploadedFile, this);
    }

    public FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws BaseException {
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
