package omnidrive.filesystem;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FileSystemTest extends BaseTest {

    @Test
    public void testManifestExistsReturnsFalseIfManifestFileDoesNotExist() throws Exception {
        // Given a root directory with no manifest file
        File root = getResource("foo");
        FileSystem fileSystem = new FileSystem(root.toPath());

        // When you check if manifest exists
        boolean result = fileSystem.manifestExists();

        // Then you get false
        assertFalse(result);
    }

    @Test
    public void testManifestExistsReturnsTrueIfManifestFileExists() throws Exception {
        // Given a root directory with a manifest file
        File root = Files.createTempDir();
        File manifest = new File(root, ".manifest");
        assertTrue(manifest.createNewFile());
        FileSystem fileSystem = new FileSystem(root.toPath());

        // When you check if manifest exists
        boolean result = fileSystem.manifestExists();

        // Then you get true
        assertTrue(result);
    }

}