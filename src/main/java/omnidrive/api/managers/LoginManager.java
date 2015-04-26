package omnidrive.api.managers;

import omnidrive.api.box.BoxApi;
import omnidrive.api.box.BoxUser;
import omnidrive.api.dropbox.*;
import omnidrive.api.base.*;
import omnidrive.api.google.GoogleDriveApi;
import omnidrive.api.google.GoogleDriveUser;
import omnidrive.api.microsoft.OneDriveApi;
import omnidrive.api.microsoft.OneDriveUser;
import omnidrive.ui.login.LoginController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class LoginManager implements PropertyChangeListener {

    private static LoginManager manager = null;

    private final DropboxApi dropbox = new DropboxApi();
    private final BoxApi box = new BoxApi();
    private final GoogleDriveApi googleDrive = new GoogleDriveApi();
    private final OneDriveApi oneDrive = new OneDriveApi();

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

    public void oneDriveLogin() throws BaseException {
        this.oneDrive.login(this);
    }

    public void boxLogin() throws BaseException {
        this.box.login(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        BaseUser user = (BaseUser)evt.getNewValue();

        switch (getSourceType(evt.getSource())) {
            case Dropbox:
                registerDropboxUser((DropboxUser)user);
                break;
            case GoogleDrive:
                registerGoogleDriveUser((GoogleDriveUser)user);
                break;
            case OneDrive:
                registerOneDriveUser((OneDriveUser)user);
                break;
            case Box:
                registerBoxUser((BoxUser)user);
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

    private void registerDropboxUser(DropboxUser user) {
        AccountsManager.getAccountsManager().setDropboxUser(user);
    }

    private void registerGoogleDriveUser(GoogleDriveUser user) {
        AccountsManager.getAccountsManager().setGoogleDriveUser(user);
    }

    private void registerOneDriveUser(OneDriveUser user) {
        AccountsManager.getAccountsManager().setOneDriveUser(user);
    }

    private void registerBoxUser(BoxUser user) {
        AccountsManager.getAccountsManager().setBoxUser(user);
    }

    private DriveType getSourceType(Object source) {
        DriveType type = null;

        if (source instanceof DropboxApi) {
            type = DriveType.Dropbox;
        } else if (source instanceof GoogleDriveApi) {
            type = DriveType.GoogleDrive;
        } else if (source instanceof OneDriveApi) {
            type = DriveType.OneDrive;
        }

        return type;
    }

    public void showError(String errorMessage) {
        //PopUpView popup = new PopUpView(errorMessage, Alert.AlertType.ERROR);
        //popup.show();
    }

    public static LoginManager getLoginManager() {
        if (manager == null) {
            manager = new LoginManager();
        }

        return manager;
    }
}
