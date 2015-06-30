package omnidrive.filesystem;

import com.google.common.io.Files;
import omnidrive.api.base.DriveType;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.util.MapDbUtils;
import org.junit.Test;
import org.mapdb.DB;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testGetValidManifest() throws Exception {
        // Given a manifest file exists
        File root = Files.createTempDir();
        FileSystem fileSystem = new FileSystem(root.toPath());
        File manifestFile = new File(root, ".manifest");
        assertTrue(manifestFile.createNewFile());
        DB db = MapDbUtils.createFileDb(manifestFile);
        Manifest manifest = new MapDbManifest(db);
        Blob blob = new Blob("foo", 10L, DriveType.Dropbox);
        manifest.put(blob);
        db.commit();

        // When you ask for the manifest
        Manifest resultManifest = fileSystem.getManifest();

        // You get a valid manifest
        assertEquals(blob, resultManifest.get("foo", Blob.class));
    }
}