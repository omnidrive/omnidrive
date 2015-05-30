package omnidrive.api.managers;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.base.*;
import omnidrive.api.auth.AuthService;

public class LoginManager implements AuthListener {

    private static LoginManager manager = null;

    private AuthService authService;

    private final ApiManager apiManager;

    // singleton
    private LoginManager() {
        this.apiManager = new ApiManager();
    }

    public void login(DriveType type, AuthService service) {

        this.authService = service;

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

    @Override
    public void authenticated(DriveType type, BaseAccount account) {
        if (this.authService != null) {
            this.authService.terminate(type, account);
        }
    }

    @Override
    public void failure(DriveType type, String error) {
        if (this.authService != null) {
            this.authService.report(type, error);
        }
    }

    public void remove(DriveType type) {
        // TODO - remove drive account from user
    }

    private void requestLogin(DriveType type, BaseApi api, String authUrl) {
        if (this.authService != null) {
            this.authService.connect(type, api, authUrl);
        }
    }

    public static LoginManager getLoginManager() {
        if (manager == null) {
            manager = new LoginManager();
        }

        return manager;
    }
}
