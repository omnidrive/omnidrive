package omnidrive.api.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import omnidrive.api.account.*;

import java.io.*;
import java.util.Arrays;

public class GoogleDriveAccount extends Account implements CredentialRefreshListener {

    private static final String MimeTypeFile = "application/vnd.google-apps.file";
    private static final String MimeTypeFolder = "application/vnd.google-apps.folder";

    private static final String GoogleFileKind = "drive#file";
    private static final String GoogleFolderKind = "drive#parentReference";

    private final Drive service;


    public GoogleDriveAccount(AccountMetadata metadata, Drive service) {
        this(metadata, service, null);
    }

    public GoogleDriveAccount(AccountMetadata metadata, Drive service, RefreshedAccountObserver observer) {
        super(AccountType.GoogleDrive, metadata, observer);
        this.service = service;
    }

    @Override
    protected void createRootFolder() throws AccountException {
        File body = new File();
        body.setTitle(OMNIDRIVE_ROOT_FOLDER_NAME);
        body.setMimeType(MimeTypeFolder);

        try {
            if (!isOmniDriveFolderExists()) {
                synchronized (mutex) {
                    this.service.files().insert(body).execute();
                }
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to create root folder.", ex);
        }
    }

    @Override
    protected String getOmniDriveFolderId() throws AccountException {
        synchronized (mutex) {
            if (this.metadata.getRootFolderId() != null) {
                return this.metadata.getRootFolderId();
            }

            try {
                String query = "title = '" + OMNIDRIVE_ROOT_FOLDER_NAME + "' and mimeType = '" + MimeTypeFolder + "'";
                Drive.Files.List request = this.service.files().list().setQ(query);

                for (File rootFolder : request.execute().getItems()) {
                    if (rootFolder.getTitle().equals(OMNIDRIVE_ROOT_FOLDER_NAME)) {
                        this.metadata.setRootFolderId(rootFolder.getId());
                        break;
                    }
                }
            } catch (IOException ex) {
                throw new GoogleDriveException("Failed to get root folder.", ex);
            }

            return this.metadata.getRootFolderId();
        }
    }

    @Override
    public void refreshAuthorization(Object object) throws AccountException {
        try {
            if (!(object instanceof Credential)) {
                throw new GoogleDriveException("Wrong type of credentials", null);
            }

            Credential credential = (Credential) object;
            if (!credential.refreshToken()) {
                throw new GoogleDriveException("Failed to refresh token", null);
            }

            synchronized (mutex) {
                this.metadata.setRefreshToken(credential.getRefreshToken());
                this.metadata.setAccessToken(credential.getAccessToken());
            }
            notifyRefreshed();
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to refresh account", ex);
        }
    }

    @Override
    public String getUsername() {
        String name;

        try {
            synchronized (mutex) {
                name = this.service.about().get().execute().getName();
            }
        } catch (IOException ex) {
            name = null;
        }

        return name;
    }

    @Override
    public String getUserId() {
        String id;

        try {
            synchronized (mutex) {
                id = this.service.about().get().execute().getUser().getPermissionId();
            }
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
            synchronized (mutex) {
                File uploadedFile = this.service.files().insert(body, mediaContent).execute();
                fileId = uploadedFile.getId();
                this.usedSize += uploadedFile.getFileSize();
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to upload file", ex);
        }

        return fileId;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws AccountException {
        long size = 0;

        try {
            ByteArrayOutputStream streamWithSize = new ByteArrayOutputStream();
            synchronized (mutex) {
                this.service.files().get(fileId).executeMediaAndDownloadTo(streamWithSize);
            }
            size = streamWithSize.size();
            streamWithSize.writeTo(outputStream);
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to download file", ex);
        }

        return size;
    }

    @Override
    public void removeFile(String fileId) throws AccountException {
        try {
            synchronized (mutex) {
                File file = this.service.files().get(fileId).execute();
                long fileSize = file.getFileSize();
                this.service.files().delete(fileId).execute();
                this.usedSize -= fileSize;
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to remove file", ex);
        }
    }

    @Override
    public void removeFolder(String fileId) throws AccountException {
        removeFile(fileId); // In the Drive API, a folder is essentially a file [https://developers.google.com/drive/web/folder]
    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws AccountException {
        try {
            synchronized (mutex) {
                File file = service.files().get(fileId).execute();
                this.usedSize -= file.getFileSize();

                AbstractInputStreamContent mediaContent = new InputStreamContent(MimeTypeFile, inputStream);

                File updatedFile = service.files().update(fileId, file, mediaContent).execute();
                if (updatedFile == null) {
                    throw new GoogleDriveException("Failed to update file", null);
                }
                this.usedSize += updatedFile.getFileSize();
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get file.", ex);
        }
    }

    public void fetchManifestId() throws AccountException {
        if (manifestExists()) {
            return;
        }

        try {
            synchronized (mutex) {
                String query = "title = '" + MANIFEST_FILE_NAME + "' and '" + getOmniDriveFolderId() + "' in parents";
                Drive.Files.List request = this.service.files().list().setQ(query);

                for (File file : request.execute().getItems()) {
                    if (file.getTitle().equals(MANIFEST_FILE_NAME)) {
                        this.metadata.setManifestId(file.getId());
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to fetch manifest file", ex);
        }
    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
        long usedQuota;

        try {
            synchronized (mutex) {
                usedQuota = this.service.about().get().execute().getQuotaBytesUsed();
                this.usedSize = usedQuota;
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get quota used size.", ex);
        }

        return usedQuota;
    }

    @Override
    public long getQuotaTotalSize() throws AccountException {
        long totalQuota;

        try {
            synchronized (mutex) {
                totalQuota = this.service.about().get().execute().getQuotaBytesTotal();
                this.totalSize = totalQuota;
            }
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to get quota total size.", ex);
        }

        return totalQuota;
    }

    @Override
    public void onTokenResponse(Credential credential, TokenResponse tokenResponse) throws IOException {
        System.out.println("GoogleDrive: refresh token");
        synchronized (mutex) {
            if (tokenResponse.getAccessToken() != null) {
                this.metadata.setAccessToken(tokenResponse.getAccessToken());
            }
            //here we always fail, because google use them same refresh_token, the refresh_token does not refreshed
            if (tokenResponse.getRefreshToken() != null) {
                this.metadata.setRefreshToken(tokenResponse.getRefreshToken());
            }
        }
        notifyRefreshed();
    }

    @Override
    public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse) throws IOException {
        System.out.println("GoogleDrive Token Error: " + tokenErrorResponse.getError());
    }
}
