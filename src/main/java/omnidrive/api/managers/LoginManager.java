package omnidrive.api.managers;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.base.*;
import omnidrive.api.auth.AuthService;

public class LoginManager implements AuthListener {

    private static LoginManager manager = null;

    private AuthService authService;

    private final AuthManager authManager = AuthManager.getAuthManager();

    // singleton
    private LoginManager() {

    }

    public void login(AccountType type, AuthService service) {

        this.authService = service;

        try {
            String authUrl = this.authManager.login(type, this);
            if (authUrl != null) {
                CloudAuthorizer api = this.authManager.getAuthorizer(type);
                requestLogin(type, api, authUrl);
            } else {
                failure(type, "Failed to get auth url.");
            }
        } catch (Exception ex) {
            failure(type, "Error occured: " + ex.getMessage());
        }
    }

    @Override
    public void authenticated(AccountType type, CloudAccount account) {
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

    private void requestLogin(AccountType type, CloudAuthorizer api, String authUrl) {
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
