package omnidrive.api.managers;

import omnidrive.api.base.*;
import omnidrive.ui.general.LoginService;

public class LoginManager implements AuthListener {

    private static LoginManager manager = null;

    private LoginService loginService;

    private final ApiManager apiManager;

    // singleton
    private LoginManager() {
        this.apiManager = new ApiManager();
    }

    public void login(DriveType type, LoginService service) {

        this.loginService = service;

        try {
            String authUrl = this.apiManager.login(type, this);
            if (authUrl != null) {
                BaseApi api = this.apiManager.getApi(type);
                requestLogin(type, api, authUrl);
            } else {
                failure(type, "Failed to get auth url.");
            }
        } catch (Exception ex) {
            failure(type, "Error occured: " + ex.getMessage());
        }
    }

    /*************************************************
     * AuthListener
     *************************************************/

    public void authenticated(DriveType type, BaseUser user) {
        if (this.loginService != null) {
            this.loginService.terminate(type, user);
        }
    }

    public void failure(DriveType type, String error) {
        if (this.loginService != null) {
            this.loginService.report(type, error);
        }
    }

    /**************************************************/

    public void remove(DriveType type) {
        // TODO - remove drive account from user
    }

    private void requestLogin(DriveType type, BaseApi api, String authUrl) {
        if (this.loginService != null) {
            this.loginService.connect(type, api, authUrl);
        }
    }

    public static LoginManager getLoginManager() {
        if (manager == null) {
            manager = new LoginManager();
        }

        return manager;
    }
}
