package omnidrive.filesystem;

import omnidrive.api.auth.AuthTokens;
import omnidrive.api.base.DriveType;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class FileSystem {

    private static final String USER_HOME = System.getProperty("user.home");

    private static final String ROOT_NAME = "OmniDrive";

    public String getRootDirectory() {
        return USER_HOME + File.pathSeparator + ROOT_NAME;
    }

    public boolean isReady() {
        return true;
    }

    public void initialize() {

    }

    public void startSync() {

    }

    public Map<DriveType, AuthTokens> getRegisteredAccounts() {
        return new TreeMap<DriveType, AuthTokens>();
    }
}
