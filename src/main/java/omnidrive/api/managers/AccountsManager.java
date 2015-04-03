package omnidrive.api.managers;

import omnidrive.api.dropbox.DropboxUser;
import omnidrive.api.google.GoogleDriveUser;
import omnidrive.api.microsoft.OneDriveUser;

public class AccountsManager {

    private static AccountsManager manager = null;

    private DropboxUser dropboxUser;
    private GoogleDriveUser googleDriveUser;
    private OneDriveUser oneDriveUser;

    // singleton
    private AccountsManager() {

    }

    public static AccountsManager getAccountsManager() {
        if (manager == null) {
            manager = new AccountsManager();
        }

        return manager;
    }

    public void setDropboxUser(DropboxUser user) {
        this.dropboxUser = user;
    }

    public DropboxUser getDropboxUser() {
        return this.dropboxUser;
    }

    public void setGoogleDriveUser(GoogleDriveUser user) {
        this.googleDriveUser = user;
    }

    public GoogleDriveUser getGoogleDriveUser() {
        return this.googleDriveUser;
    }

    public void setOneDriveUser(OneDriveUser user) {
        this.oneDriveUser = user;
    }

    public OneDriveUser getOneDriveUser() {
        return this.oneDriveUser;
    }
}
