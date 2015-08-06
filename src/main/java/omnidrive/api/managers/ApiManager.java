package omnidrive.api.managers;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.base.CloudApi;
import omnidrive.api.base.AccountType;
import omnidrive.api.box.BoxApi;
import omnidrive.api.dropbox.DropboxApi;
import omnidrive.api.google.GoogleDriveApi;

public class ApiManager {

    private final CloudApi[] apis = new CloudApi[AccountType.length()];

    private static ApiManager apiManager = null;

    private ApiManager() {
        for (AccountType type : AccountType.values()) {
            apis[type.ordinal()] = createApi(type);
        }
    }

    public static ApiManager getApiManager() {
        if (apiManager == null) {
            apiManager = new ApiManager();
        }

        return apiManager;
    }

    private CloudApi createApi(AccountType type) {
        CloudApi api = null;

        switch (type) {
            case Dropbox:
                api = new DropboxApi();
                break;
            case GoogleDrive:
                api = new GoogleDriveApi();
                break;
            case Box:
                api = new BoxApi();
                break;
        }

        return api;
    }

    public String login(AccountType type, AuthListener listener) throws Exception {
        String authUrl = null;

        if (this.apis[type.ordinal()] != null) {
            authUrl = this.apis[type.ordinal()].login(listener);
        }

        return authUrl;
    }

    public CloudApi getApi(AccountType type) {
        return this.apis[type.ordinal()];
    }

    public static AccountType toType(CloudApi api) {
        AccountType type = null;

        if (api instanceof DropboxApi) {
            type = AccountType.Dropbox;
        } else if (api instanceof GoogleDriveApi) {
            type = AccountType.GoogleDrive;
        } else if (api instanceof BoxApi) {
            type = AccountType.Box;
        }

        return type;
    }
}
