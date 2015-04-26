package omnidrive.api.managers;

import java.util.ArrayList;
import java.util.List;

import omnidrive.api.base.AuthListener;
import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseException;
import omnidrive.api.base.DriveType;
import omnidrive.api.box.BoxApi;
import omnidrive.api.dropbox.DropboxApi;
import omnidrive.api.google.GoogleDriveApi;

public class ApiManager {

    private final List<BaseApi> apis;

    public ApiManager() {
        this.apis = new ArrayList<BaseApi>(DriveType.values().length);

        for (DriveType type : DriveType.values()) {
            apis.add(type.ordinal(), createApi(type));
        }
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
        return this.apis.get(type.ordinal()).login(listener);
    }

    public BaseApi getApi(DriveType type) {
        return this.apis.get(type.ordinal());
    }
}
