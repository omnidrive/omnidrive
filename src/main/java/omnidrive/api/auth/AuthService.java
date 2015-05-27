package omnidrive.api.auth;

import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseUser;
import omnidrive.api.base.DriveType;

public interface AuthService {

    void connect(DriveType type, BaseApi api, String authUrl);


    void report(DriveType type, String message);


    void terminate(DriveType type, BaseUser user);

}
