package omnidrive.api.auth;

import omnidrive.api.base.BaseUser;
import omnidrive.api.base.DriveType;

public interface AuthListener {

    void authenticated(DriveType type, BaseUser user);

    void failure(DriveType type, String error);

}
