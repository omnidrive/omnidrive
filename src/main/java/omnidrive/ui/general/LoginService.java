package omnidrive.ui.general;

import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.DriveType;

public interface LoginService {

    void connect(DriveType type, BaseApi api, String authUrl);


    void report(DriveType type, String message);


    void terminate(DriveType type, BaseAccount account);

}
