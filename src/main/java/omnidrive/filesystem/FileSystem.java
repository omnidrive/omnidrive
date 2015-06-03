package omnidrive.filesystem;

import omnidrive.api.auth.AuthTokens;
import omnidrive.api.base.DriveType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystem {

    private static final String USER_HOME = System.getProperty("user.home");

    private static final String ROOT_NAME = "OmniDrive";

    public Path getRootPath() {
        return Paths.get(USER_HOME, ROOT_NAME);
    }

    public boolean isReady() {
        return Files.isDirectory(getRootPath());
    }

    public void initialize() throws IOException {
        Files.createDirectory(getRootPath());
    }

    public void startSync() {

    }

    public Map<DriveType, AuthTokens> getRegisteredAccounts() {
        return new TreeMap<DriveType, AuthTokens>();
    }
}
