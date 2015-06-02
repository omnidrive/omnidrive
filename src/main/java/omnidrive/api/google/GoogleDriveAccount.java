package omnidrive.api.google;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import omnidrive.api.base.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.api.services.drive.model.File;

public class GoogleDriveAccount extends BaseAccount {

    private static final String MimeTypeFile = "application/vnd.google-apps.file";
    private static final String MimeTypeFolder = "application/vnd.google-apps.folder";

    private static final String GoogleFileKind = "drive#file";
    private static final String GoogleFolderKind = "drive#parentReference";

    private Drive service;

    public GoogleDriveAccount(Drive service) {
        this.service = service;
    }

    @Override
    public String getName() {
        return "drive";
    }

    @Override
    protected void createRootFolder() throws BaseException {
        File body = new File();
        body.setTitle(ROOT_FOLDER_NAME);
        body.setMimeType(MimeTypeFolder);

        GoogleDriveFolder folder = null;
        GoogleDriveFolder rootFolder = null;

        try {
            String rootFolderId = this.service.about().get().execute().getRootFolderId();
            com.google.api.services.drive.model.File googleRootFolder = this.service.files().get(rootFolderId).execute();

            java.io.File fileContent = new java.io.File(googleRootFolder.getTitle());
            FileContent mediaContent = new FileContent(MimeTypeFolder, fileContent);
            this.service.files().insert(body, mediaContent).execute();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to create root folder.");
        }
    }

    @Override
    public String getUsername() {
        String name;

        try {
            name = this.service.about().get().execute().getName();
        } catch (IOException ex) {
            name = null;
        }

        return name;
    }

    @Override
    public String getUserId() {
        String id;

        try {
            id = this.service.about().get().execute().getUser().getPermissionId();
        } catch (IOException ex) {
            id = null;
        }

        return id;
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) throws BaseException {
        String fileId = null;

        File body = new File();
        body.setTitle(name);

        AbstractInputStreamContent mediaContent = new InputStreamContent(MimeTypeFile, inputStream);

        try {
            File uploadedFile = this.service.files().insert(body, mediaContent).execute();
            fileId = uploadedFile.getId();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to upload file");
        }

        return fileId;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws BaseException {
        long size = 0;

        try {
            InputStream inputStream = this.service.files().get(fileId).executeAsInputStream();

            while (inputStream.available() > 0) {
                outputStream.write(inputStream.read());
                size++;
            }

            inputStream.close();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to download file");
        }

        return size;
    }

    @Override
    public void removeFile(String fileId) throws BaseException {
        try {
            this.service.files().delete(fileId).execute();
        } catch (IOException ex) {
            throw new GoogleDriveException(ex.getMessage());
        }
    }

    @Override
    public void removeFolder(String fileId) throws BaseException {
        removeFile(fileId); // In the Drive API, a folder is essentially a file [https://developers.google.com/drive/web/folder]
    }

    @Override
    public long getQuotaUsedSize() throws BaseException {
        long usedQuota;

        try {
            usedQuota = this.service.about().get().execute().getQuotaBytesUsed();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get quota used size.");
        }

        return usedQuota;
    }

    @Override
    public long getQuotaTotalSize() throws BaseException {
        long totalQuota;

        try {
            totalQuota = this.service.about().get().execute().getQuotaBytesTotal();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get quota total size.");
        }

        return totalQuota;
    }
}
