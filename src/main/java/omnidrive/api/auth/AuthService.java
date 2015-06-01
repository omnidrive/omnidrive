package omnidrive.api.auth;

import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.DriveType;

public interface AuthService {

    void attempt(DriveType type, BaseApi api, String authUrl);


    void report(DriveType type, String message);


    void succeed(DriveType type, BaseAccount account);

}
