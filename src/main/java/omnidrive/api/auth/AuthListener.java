package omnidrive.api.auth;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.DriveType;

public interface AuthListener {

    void authenticated(DriveType type, BaseAccount account);

    void failure(DriveType type, String error);

}
