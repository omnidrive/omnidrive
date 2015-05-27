package omnidrive.api.base;

public interface AuthListener {

    void authenticated(DriveType type, BaseUser user);

    void failure(DriveType type, String error);

}
