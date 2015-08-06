package omnidrive.api.google;

import com.google.api.client.http.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;

import java.io.*;
import java.util.Arrays;

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
    protected void createRootFolder() throws BaseException {
        File body = new File();
        body.setTitle(OMNIDRIVE_ROOT_FOLDER_NAME);
        body.setMimeType(MimeTypeFolder);

        try {
            if (!isOmniDriveFolderExists()) {
                this.service.files().insert(body).execute();
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to create root folder.");
        }
    }

    @Override
    protected String getOmniDriveFolderId() throws BaseException {
        if (this.omniDriveFolderId != null) {
            return this.omniDriveFolderId;
        }

        try {
            String query = "title = '" + OMNIDRIVE_ROOT_FOLDER_NAME + "' and mimeType = '" + MimeTypeFolder + "'";
            Drive.Files.List request = this.service.files().list().setQ(query);

            for (File rootFolder : request.execute().getItems()) {
                if (rootFolder.getTitle().equals(OMNIDRIVE_ROOT_FOLDER_NAME)) {
                    this.omniDriveFolderId = rootFolder.getId();
                    break;
                }
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get root folder.");
        }

        return this.omniDriveFolderId;
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
        body.setMimeType("*/*");
        body.setParents(Arrays.asList(new ParentReference().setId(getOmniDriveFolderId())));

        AbstractInputStreamContent mediaContent = new InputStreamContent(MimeTypeFile, inputStream);

        try {
            File uploadedFile = this.service.files().insert(body, mediaContent).execute();
            fileId = uploadedFile.getId();
            this.usedSize += uploadedFile.getFileSize();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to upload file");
        }

        return fileId;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws BaseException {
        long size = 0;

        try {
            ByteArrayOutputStream streamWithSize = new ByteArrayOutputStream();
            this.service.files().get(fileId).executeMediaAndDownloadTo(streamWithSize);
            size = streamWithSize.size();
            streamWithSize.writeTo(outputStream);
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to download file");
        }

        return size;
    }

    @Override
    public void removeFile(String fileId) throws BaseException {
        try {
            File file = this.service.files().get(fileId).execute();
            long fileSize = file.getFileSize();
            this.service.files().delete(fileId).execute();
            this.usedSize -= fileSize;
        } catch (IOException ex) {
            throw new GoogleDriveException(ex.getMessage());
        }
    }

    @Override
    public void removeFolder(String fileId) throws BaseException {
        removeFile(fileId); // In the Drive API, a folder is essentially a file [https://developers.google.com/drive/web/folder]
    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws BaseException {
        try {
            // First retrieve the file from the API.
            File file = service.files().get(fileId).execute();
            this.usedSize -= file.getFileSize();

            // File's new content.
            AbstractInputStreamContent mediaContent = new InputStreamContent(MimeTypeFile, inputStream);

            // Send the request to the API.
            File updatedFile = service.files().update(fileId, file, mediaContent).execute();
            if (updatedFile == null) {
                throw new GoogleDriveException("Failed to update file");
            }
            this.usedSize += updatedFile.getFileSize();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get file.");
        }
    }

    @Override
    public long downloadManifestFile(OutputStream outputStream) throws BaseException {
        long size = 0;

        if (!isOmniDriveFolderExists()) {
            throw new GoogleDriveException("No 'OmniDrive' root folder exists");
        }

        try {
            String query = "title = '" + MANIFEST_FILE_NAME + "' and '" + getOmniDriveFolderId() + "' in parents";
            Drive.Files.List request = this.service.files().list().setQ(query);

            FileList files = request.execute();
            for (File manifestFile : files.getItems()) {
                size = manifestFile.getFileSize();
                this.service.files().get(manifestFile.getId()).executeMediaAndDownloadTo(outputStream);
                break;
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to download manifest file.");
        }

        return size;
    }

    public void uploadManifest(InputStream inputStream, long size) throws BaseException {
        uploadFile(MANIFEST_FILE_NAME, inputStream, size);
    }

    public void updateManifest(InputStream inputStream, long size) throws BaseException {
        if (this.manifestFileId == null) {
            throw new GoogleDriveException("Manifest file id does not exist");
        }

        updateFile(this.manifestFileId, inputStream, size);
    }

    public boolean manifestExists() throws BaseException {
        boolean exists = false;

        try {
            HttpResponse response = this.service.parents().get(this.manifestFileId, getOmniDriveFolderId()).executeUsingHead();
            if (response.getStatusCode() == 200) {
                exists = true;
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to fetch manifest file");
        }

        return exists;
    }

    @Override
    public long getQuotaUsedSize() throws BaseException {
        long usedQuota;

        try {
            usedQuota = this.service.about().get().execute().getQuotaBytesUsed();
            this.usedSize = usedQuota;
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
            this.totalSize = totalQuota;
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get quota total size.");
        }

        return totalQuota;
    }
}
