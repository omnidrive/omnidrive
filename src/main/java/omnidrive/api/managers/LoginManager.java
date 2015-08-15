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
                AccountAuthorizer api = this.authManager.getAuthorizer(type);
                requestLogin(type, api, authUrl);
            } else {
                authFailure(type, "Failed to get auth url.");
            }
        } catch (Exception ex) {
            authFailure(type, "Error occured: " + ex.getMessage());
        }
    }

    @Override
    public void authSucceed(AccountType type, Account account) {
        if (this.authService != null) {
            this.authService.accountAuthorized(type, account);
        }
    }

    @Override
    public void authFailure(AccountType type, String error) {
        if (this.authService != null) {
            this.authService.reportAuthError(type, error);
        }
    }

    public void remove(AccountType type) {
        // TODO - remove drive account from user
    }

    private void requestLogin(AccountType type, AccountAuthorizer api, String authUrl) {
        if (this.authService != null) {
            this.authService.attemptToAuth(type, api, authUrl);
        }
    }

    public static LoginManager getLoginManager() {
        if (manager == null) {
            manager = new LoginManager();
        }

        return manager;
    }
}
