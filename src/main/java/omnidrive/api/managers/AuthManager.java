package omnidrive.api.managers;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.base.CloudAuthorizer;
import omnidrive.api.base.AccountType;
import omnidrive.api.box.BoxAuthorizer;
import omnidrive.api.dropbox.DropboxAuthorizer;
import omnidrive.api.google.GoogleDriveAuthorizer;

public class AuthManager {

    private final CloudAuthorizer[] authorizers = new CloudAuthorizer[AccountType.length()];

    private static AuthManager authManager = null;

    private AuthManager() {
        for (AccountType type : AccountType.values()) {
            authorizers[type.ordinal()] = createApi(type);
        }
    }

    public static AuthManager getAuthManager() {
        if (authManager == null) {
            authManager = new AuthManager();
        }

        return authManager;
    }

    private CloudAuthorizer createApi(AccountType type) {
        CloudAuthorizer authorizer = null;

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

    public CloudAuthorizer getAuthorizer(AccountType type) {
        return this.authorizers[type.ordinal()];
    }

    public static AccountType toType(CloudAuthorizer authorizer) {
        AccountType type = null;

        if (authorizer instanceof DropboxAuthorizer) {
            type = AccountType.Dropbox;
        } else if (authorizer instanceof GoogleDriveAuthorizer) {
            type = AccountType.GoogleDrive;
        } else if (authorizer instanceof BoxAuthorizer) {
            type = AccountType.Box;
        }

        return type;
    }
}
