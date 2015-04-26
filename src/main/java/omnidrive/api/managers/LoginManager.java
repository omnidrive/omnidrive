package omnidrive.api.managers;

import omnidrive.api.base.*;
import omnidrive.ui.login.LoginController;

public class LoginManager implements AuthListener {

    private static LoginManager manager = null;

    private LoginController loginController;

    private final ApiManager apiManager = new ApiManager();

    // singleton
    private LoginManager() {

    }

    public void login(DriveType type) {
        try {
            String authUrl = this.apiManager.login(type, this);
            if (authUrl != null) {
                BaseApi api = this.apiManager.getApi(type);
                showLoginView(api, authUrl);
            } else {
                showError("Failed to login.");
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }


    public void register(DriveType type, BaseUser user) {
        registerUser(type, user);

        if (this.loginController != null) {
            this.loginController.closeLoginWebView();
        }
    }

    public void showLoginView(final Authorizer auth, String authUrl) {
        if (this.loginController != null) {
            this.loginController.showLoginWebView(auth, authUrl);
        }
    }

    private void registerUser(DriveType type, BaseUser user) {
        AccountsManager.getAccountsManager().setLoggedInUser(type, user);
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
