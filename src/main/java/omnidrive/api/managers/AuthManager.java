package omnidrive.api.managers;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.base.AccountAuthorizer;
import omnidrive.api.base.AccountType;
import omnidrive.api.box.BoxAuthorizer;
import omnidrive.api.dropbox.DropboxAuthorizer;
import omnidrive.api.google.GoogleDriveAuthorizer;

public class AuthManager {

    private final AccountAuthorizer[] authorizers = new AccountAuthorizer[AccountType.length()];

    private static AuthManager authManager = null;

    private AuthManager() {
        for (AccountType type : AccountType.values()) {
            authorizers[type.ordinal()] = createAuthorizer(type);
        }
    }

    public static AuthManager getAuthManager() {
        if (authManager == null) {
            authManager = new AuthManager();
        }

        return authManager;
    }

    private AccountAuthorizer createAuthorizer(AccountType type) {
        AccountAuthorizer authorizer = null;

        switch (type) {
            case Dropbox:
                authorizer = new DropboxAuthorizer();
                break;
            case GoogleDrive:
                authorizer = new GoogleDriveAuthorizer();
                break;
            case Box:
                authorizer = new BoxAuthorizer();
                break;
            case OneDrive:
                //authorizer = new OneDriveAuthorizer();
                break;
        }

        return authorizer;
    }

    public String login(AccountType type, AuthListener listener) throws Exception {
        String authUrl = null;

        if (this.authorizers[type.ordinal()] != null) {
            authUrl = this.authorizers[type.ordinal()].login(listener);
        }

        return authUrl;
    }

    public AccountAuthorizer getAuthorizer(AccountType type) {
        return this.authorizers[type.ordinal()];
    }

    public static AccountType toType(AccountAuthorizer authorizer) {
        AccountType type = null;

        if (authorizer instanceof DropboxAuthorizer) {
            type = AccountType.Dropbox;
        } else if (authorizer instanceof GoogleDriveAuthorizer) {
            type = AccountType.GoogleDrive;
        } else if (authorizer instanceof BoxAuthorizer) {
            type = AccountType.Box;
        }
        /*} else if (authorizer instanceof OneDriveAuthorizer) {
            type = AccountType.OneDrive;
        }*/

        return type;
    }
}
