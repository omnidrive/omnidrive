package omnidrive.api.managers;

import com.google.api.services.drive.Drive;
import omnidrive.api.dropbox.*;
import omnidrive.api.base.*;
import omnidrive.api.googledrive.GoogleDriveApi;
import omnidrive.ui.login.LoginController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class LoginManager implements PropertyChangeListener {

    private static LoginManager manager = null;

    private final DropboxApi dropbox = new DropboxApi();
    private final GoogleDriveApi googleDrive = new GoogleDriveApi();

    private LoginController loginController;

    // singleton
    private LoginManager() {

    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public void dropboxLogin() throws BaseException {
        this.dropbox.login(this);
    }

    public void googleDriveLogin() throws BaseException {
        this.googleDrive.login(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        switch (getSourceType(evt.getSource())) {
            case Dropbox:
                String accessToken = (String) evt.getNewValue();
                registerDropboxUser(accessToken);
                break;
            case GoogleDrive:
                Drive service = (Drive)evt.getNewValue();
                registerGoogleDriveUser(service);
                break;
            case OneDrive:
                break;
        }

        if (this.loginController != null) {
            this.loginController.closeLoginWebView();
        }
    }

    public void showLoginView(final BaseApi api, String authUrl) {
        if (this.loginController != null) {
            this.loginController.showLoginWebView(api, authUrl);
        }
    }

    private void registerDropboxUser(String accessToken) {
        AccountsManager.getAccountsManager().setDropboxUser(this.dropbox.getConfig(), accessToken);
    }

    private void registerGoogleDriveUser(Drive service) {
        AccountsManager.getAccountsManager().setGoogleDriveUser(service);
    }

    private DriveType getSourceType(Object source) {
        DriveType type = null;

        if (source instanceof DropboxApi) {
            type = DriveType.Dropbox;
        } else if (source instanceof GoogleDriveApi) {
            type = DriveType.GoogleDrive;
        } /*else if (source instanceof OneDriveApi) {
            type = DriveType.OneDrive;
        }*/

        return type;
    }

    public static LoginManager getLoginManager() {
        if (manager == null) {
            manager = new LoginManager();
        }

        return manager;
    }
}
