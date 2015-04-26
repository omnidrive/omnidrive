package omnidrive.api.google;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import omnidrive.api.base.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
            id = this.service.about().get().execute().getUser().getPermissionId();
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

    public FileOutputStream downloadFile(String remoteSrcId, String localDestPath) throws BaseException {
        FileOutputStream outStream = null;
        try {
            InputStream inputStream = this.service.files().get(remoteSrcId).executeAsInputStream();
            outStream = new FileOutputStream(localDestPath);

            while (inputStream.available() > 0) {
                outStream.write(inputStream.read());
            }

            inputStream.close();
            outStream.close();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to insert file");
        }

        return outStream;
    }

    public BaseFolder createFolder(String remoteParentPath, String folderName) throws BaseException {
        return null;
    }

    public BaseFile getFile(String remoteId) throws BaseException {
        GoogleDriveFile file = null;

        try {
            com.google.api.services.drive.model.File googleFile = this.service.files().get(remoteId).execute();
            file = new GoogleDriveFile(googleFile, this);
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get file info");
        }

        return file;
    }

    public BaseFolder getFolder(String remoteId) throws BaseException {
        GoogleDriveFolder folder = null;

        try {
            // FIXME - not sure that 'null' is OK, needed file id.
            com.google.api.services.drive.model.ParentReference parent = this.service.parents().get(null, remoteId).execute();
            folder = new GoogleDriveFolder(parent, this);
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get file info");
        }

        return folder;
    }

}
