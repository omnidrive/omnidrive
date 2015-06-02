package omnidrive.api.managers;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.base.BaseApi;
import omnidrive.api.base.DriveType;
import omnidrive.api.box.BoxApi;
import omnidrive.api.dropbox.DropboxApi;
import omnidrive.api.google.GoogleDriveApi;

public class ApiManager {

    private final BaseApi[] apis = new BaseApi[DriveType.length()];

    private static ApiManager apiManager = null;

    private ApiManager() {
        for (DriveType type : DriveType.values()) {
            apis[type.ordinal()] = createApi(type);
        }
    }

    public static ApiManager getApiManager() {
        if (apiManager == null) {
            apiManager = new ApiManager();
        }

        return apiManager;
    }

    private BaseApi createApi(DriveType type) {
        BaseApi api = null;

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

    public String login(DriveType type, AuthListener listener) throws Exception {
        String authUrl = null;

        if (this.apis[type.ordinal()] != null) {
            authUrl = this.apis[type.ordinal()].login(listener);
        }

        return authUrl;
    }

    public BaseApi getApi(DriveType type) {
        return this.apis[type.ordinal()];
    }

    public static DriveType toType(BaseApi api) {
        DriveType type = null;

        if (api instanceof DropboxApi) {
            type = DriveType.Dropbox;
        } else if (api instanceof GoogleDriveApi) {
            type = DriveType.GoogleDrive;
        } else if (api instanceof BoxApi) {
            type = DriveType.Box;
        }

        return type;
    }
}
