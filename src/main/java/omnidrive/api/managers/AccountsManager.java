package omnidrive.api.managers;

import com.dropbox.core.DbxRequestConfig;
import com.google.api.services.drive.Drive;
import omnidrive.api.dropbox.*;
import omnidrive.api.google.GoogleDriveUser;

public class AccountsManager {

    private static AccountsManager manager = null;

    private DropboxUser dropboxUser;
    private GoogleDriveUser googleDriveUser;
    //private OneDriveUser oneDriveUser;

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

}
