package omnidrive.filesystem.sync;

import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.BaseTest;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.stub.Account;
import omnidrive.util.MapDbUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class SyncerTest extends BaseTest {

    public static final DriveType DRIVE_TYPE = DriveType.Dropbox;

    private AccountsManager accountsManager = new AccountsManager();

    private Account account = new Account(100);

    @Before
    public void setUp() throws Exception {
        accountsManager.setAccount(DRIVE_TYPE, account);
    }

    @Test
    public void testFullSyncDownloadOneFileIfNotExistsInRoot() throws Exception {
        String fileId = "foo";
        String fileContents = "Hello World";
        String fileName = "foo.txt";

        // Given a manifest with one file at root
        Manifest manifest = createInMemoryManifest();
        Tree rootTree = manifest.getRoot();
        manifest.put(new Blob(fileId, 10, DRIVE_TYPE));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, 0));
        manifest.put(rootTree);

        // And file is not in root
        Path rootPath = Files.createTempDirectory("temp_root");

        // When performing a full sync
        account.addFile(fileId, fileContents);
        Syncer syncer = new Syncer(rootPath, account);
        syncer.fullSync(manifest);

        // Then file should be downloaded
        File[] files = getFilesInPath(rootPath);
        File file = files[0];
        assertEquals(1, files.length);
        assertEquals(fileName, file.getName());
        assertEquals(fileContents, FileUtils.readFileToString(file));

        cleanup(rootPath);
    }

    @Test
    public void testFullSyncDownloadFileIfManifestHasNewerVersion() throws Exception {
        String fileName = "foo.txt";
        String oldFileContents = "old content";
        String fileId = "foo";
        String fileContents = "Hello World";

        // Given there is a file with in root
        Path rootPath = Files.createTempDirectory("temp_root");
        File oldFile = rootPath.resolve(fileName).toFile();
        FileUtils.writeStringToFile(oldFile, oldFileContents);

        // And a manifest with a newer version of the file
        Manifest manifest = createInMemoryManifest();
        Tree rootTree = manifest.getRoot();
        manifest.put(new Blob(fileId, 10, DRIVE_TYPE));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, oldFile.lastModified() + 1));
        manifest.put(rootTree);

        // When performing a full sync
        account.addFile(fileId, fileContents);
        Syncer syncer = new Syncer(rootPath, account);
        syncer.fullSync(manifest);

        // Then newer version should be downloaded
        File[] files = getFilesInPath(rootPath);
        assertEquals(fileContents, FileUtils.readFileToString(files[0]));

        cleanup(rootPath);
    }

    @Test
    public void testFullSyncDoesNotDownloadFileIfManifestHasOlderVersion() throws Exception {
        String fileName = "foo.txt";
        String oldFileContents = "old content";
        String fileId = "foo";
        String fileContents = "Hello World";

        // Given there is a file with in root
        Path rootPath = Files.createTempDirectory("temp_root");
        File oldFile = rootPath.resolve(fileName).toFile();
        FileUtils.writeStringToFile(oldFile, oldFileContents);

        // And a manifest with an older version of the file
        Manifest manifest = createInMemoryManifest();
        Tree rootTree = manifest.getRoot();
        manifest.put(new Blob(fileId, 10, DRIVE_TYPE));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, 0));
        manifest.put(rootTree);

        // When performing a full sync
        account.addFile(fileId, fileContents);
        Syncer syncer = new Syncer(rootPath, account);
        syncer.fullSync(manifest);

        // Then newer version should not be downloaded
        File[] files = getFilesInPath(rootPath);
        assertEquals(oldFileContents, FileUtils.readFileToString(files[0]));

        cleanup(rootPath);
    }

    private Manifest createInMemoryManifest() {
        DB db = MapDbUtils.createMemoryDb();
        return new MapDbManifest(db);
    }

    private void cleanup(Path path) throws IOException {
        FileUtils.deleteDirectory(path.toFile());
    }

    private File[] getFilesInPath(Path path) {
        File file = path.toFile();
        File[] files = file.listFiles();
        assert files != null;
        return files;
    }

}
