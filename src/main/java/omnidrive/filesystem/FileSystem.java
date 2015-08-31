package omnidrive.filesystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystem {

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String MANIFEST_FILENAME = ".manifest";

    public static final String ROOT_NAME = "OmniDrive";

    private final Path root;

    public FileSystem(Path root) throws Exception {
        this.root = root;
        initialize();
    }

    private void initialize() throws Exception {
        if (!this.root.toFile().exists()) {
            if (!this.root.toFile().mkdir()) {
                throw new Exception("Failed to create root: " + this.root.toString());
            }
        }
    }

    public Path getRootPath() {
        return root;
    }

    public boolean manifestExists() {
        File manifest = getManifestFile();
        return manifest.isFile();
    }

    public File getManifestFile() {
        return root.resolve(MANIFEST_FILENAME).toFile();
    }

    public static Path defaultRootPath() {
        return Paths.get(USER_HOME, ROOT_NAME);
    }

}
