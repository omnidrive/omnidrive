package omnidrive.filesystem;

import java.io.File;

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

}
