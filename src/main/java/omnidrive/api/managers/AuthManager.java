package omnidrive.api.managers;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.account.AccountAuthorizer;
import omnidrive.api.account.AccountType;
import omnidrive.api.box.BoxAuthorizer;
import omnidrive.api.dropbox.DropboxAuthorizer;
import omnidrive.api.google.GoogleDriveAuthorizer;
import omnidrive.api.microsoft.OneDriveAuthorizer;

public class AuthManager {

    private final AccountAuthorizer[] authorizers = new AccountAuthorizer[AccountType.length()];
    private final AuthSecretFile secretFile;

    private static AuthManager authManager = null;

    private AuthManager() {
        secretFile = new AuthSecretFile().analyze();

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
                authorizer = new DropboxAuthorizer(secretFile);
                break;
            case GoogleDrive:
                authorizer = new GoogleDriveAuthorizer(secretFile);
                break;
            case Box:
                authorizer = new BoxAuthorizer(secretFile);
                break;
            case OneDrive:
                authorizer = new OneDriveAuthorizer(secretFile);
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
        } else if (authorizer instanceof OneDriveAuthorizer) {
            type = AccountType.OneDrive;
        }

        return type;
    }
}
