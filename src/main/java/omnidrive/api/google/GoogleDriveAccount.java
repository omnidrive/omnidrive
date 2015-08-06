package omnidrive.api.google;

import com.google.api.client.http.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import omnidrive.api.base.Account;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountType;

import java.io.*;
import java.util.Arrays;

public class GoogleDriveAccount extends Account {

    private static final String MimeTypeFile = "application/vnd.google-apps.file";
    private static final String MimeTypeFolder = "application/vnd.google-apps.folder";

    private static final String GoogleFileKind = "drive#file";
    private static final String GoogleFolderKind = "drive#parentReference";

    private Drive service;

    public GoogleDriveAccount(Drive service) {
        this.service = service;
    }

    @Override
    protected void createRootFolder() throws AccountException {
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
    protected String getOmniDriveFolderId() throws AccountException {
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
    public String uploadFile(String name, InputStream inputStream, long size) throws AccountException {
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
    public long downloadFile(String fileId, OutputStream outputStream) throws AccountException {
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
    public void removeFile(String fileId) throws AccountException {
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
    public void removeFolder(String fileId) throws AccountException {
        removeFile(fileId); // In the Drive API, a folder is essentially a file [https://developers.google.com/drive/web/folder]
    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws AccountException {
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
    public long downloadManifest(OutputStream outputStream) throws AccountException {
        long size = 0;

        if (!isOmniDriveFolderExists()) {
            throw new GoogleDriveException("No 'OmniDrive' root folder exists");
        }

        try {
            if (hasManifestId()) {
                size = downloadFile(this.manifestFileId, outputStream);
            } else {
                String query = "title = '" + MANIFEST_FILE_NAME + "' and '" + getOmniDriveFolderId() + "' in parents";
                Drive.Files.List request = this.service.files().list().setQ(query);

                FileList files = request.execute();
                for (File manifestFile : files.getItems()) {
                    size = manifestFile.getFileSize();
                    this.service.files().get(manifestFile.getId()).executeMediaAndDownloadTo(outputStream);
                    this.manifestFileId = manifestFile.getId();
                    break;
                }
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to download manifest file.");
        }

        return size;
    }

    public void uploadManifest(InputStream inputStream, long size) throws AccountException {
        this.manifestFileId = uploadFile(MANIFEST_FILE_NAME, inputStream, size);
    }

    public void updateManifest(InputStream inputStream, long size) throws AccountException {
        if (!hasManifestId()) {
            throw new GoogleDriveException("Manifest file id does not exist");
        }

        updateFile(this.manifestFileId, inputStream, size);
    }

    @Override
    public void removeManifest() throws AccountException {
        removeManifest(AccountType.GoogleDrive);
    }

    public boolean manifestExists() throws AccountException {
        boolean exists = false;

        if (hasManifestId()) {
            return true;
        }

        try {
            String query = "title = '" + MANIFEST_FILE_NAME + "' and '" + getOmniDriveFolderId() + "' in parents";
            Drive.Files.List request = this.service.files().list().setQ(query);

            for (File file : request.execute().getItems()) {
                if (file.getTitle().equals(MANIFEST_FILE_NAME)) {
                    this.manifestFileId = file.getId();
                    exists = true;
                    break;
                }
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to fetch manifest file");
        }

        return exists;
    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
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
    public long getQuotaTotalSize() throws AccountException {
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
