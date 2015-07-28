package omnidrive.api.managers;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.base.*;
import omnidrive.api.auth.AuthService;

public class LoginManager implements AuthListener {

    private static LoginManager manager = null;

    private AuthService authService;

    private final ApiManager apiManager = ApiManager.getApiManager();

    // singleton
    private LoginManager() {

    }

    public void login(AccountType type, AuthService service) {

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
    public void authenticated(AccountType type, BaseAccount account) {
        if (this.authService != null) {
            this.authService.succeed(type, account);
        }
    }

    @Override
    public void failure(AccountType type, String error) {
        if (this.authService != null) {
            this.authService.report(type, error);
        }
    }

    public void remove(AccountType type) {
        // TODO - remove drive account from user
    }

    private void requestLogin(AccountType type, BaseApi api, String authUrl) {
        if (this.authService != null) {
            this.authService.attempt(type, api, authUrl);
        }
    }

    public static LoginManager getLoginManager() {
        if (manager == null) {
            manager = new LoginManager();
        }

        return manager;
    }
}
