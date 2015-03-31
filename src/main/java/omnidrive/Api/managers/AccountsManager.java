package omnidrive.Api.managers;

import com.dropbox.core.DbxRequestConfig;
import com.google.api.services.drive.Drive;
import omnidrive.Api.Dropbox.*;
import omnidrive.Api.Base.*;
import omnidrive.Api.googledrive.GoogleDriveUser;

public class AccountsManager {

    private static AccountsManager manager = null;

    private DropboxUser dropboxUser;
    private GoogleDriveUser googleDriveUser;
    //private OneDriveUser oneDriveUser;

    private final UploadManager uploadManager = new UploadManager();
    private final DownloadManager downloadManager = new DownloadManager();

    // singleton
    private AccountsManager() {

    }

    public static AccountsManager getAccountsManager() {
        if (manager == null) {
            manager = new AccountsManager();
        }

        return manager;
    }

    public void setDropboxUser(DbxRequestConfig config, String accessToken) {
        this.dropboxUser = new DropboxUser(config, accessToken);
    }

    public DropboxUser getDropboxUser() {
        return this.dropboxUser;
    }

    public void setGoogleDriveUser(Drive service) {
        this.googleDriveUser = new GoogleDriveUser(service);
    }

    public GoogleDriveUser getGoogleDriveUser() {
        return this.googleDriveUser;
    }


    public void uploadFile(String localFilePath) throws BaseException {
        this.uploadManager.uploadFile(this, localFilePath);
    }

    public void downloadFile(String remoteFilePath) throws BaseException {
        this.downloadManager.downloadFile(this, remoteFilePath);
    }

}
