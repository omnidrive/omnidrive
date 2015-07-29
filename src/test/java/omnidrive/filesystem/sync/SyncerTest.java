package omnidrive.filesystem.sync;

import omnidrive.api.base.AccountType;
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
import org.junit.Ignore;
import org.junit.Test;
import org.mapdb.DB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SyncerTest extends BaseTest {

    public static final AccountType DRIVE_TYPE = AccountType.Dropbox;

    private AccountsManager accountsManager = new AccountsManager();

    private Account account = new Account();

    private Manifest manifest = createMemoryManifest();

    @Before
    public void setUp() throws Exception {
        accountsManager.setAccount(DRIVE_TYPE, account);
    }

    @Test
    public void testFileExistsInManifestAndNotInFileSystem() throws Exception {
        String fileId = "foo";
        String fileContents = "Hello World";
        String fileName = "foo.txt";

        // Given a manifest with one file at root
        Tree rootTree = manifest.getRoot();
        manifest.put(new Blob(fileId, 10, DRIVE_TYPE));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, 0));
        manifest.put(rootTree);

        // And file is not in root
        Path rootPath = Files.createTempDirectory("temp_root");

        // When performing a full sync
        account.addFile(fileId, fileContents);
        Syncer syncer = new Syncer(rootPath, accountsManager);
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
    public void testFileExistsInBothManifestAndFileSystemAndManifestIsMoreRecent() throws Exception {
        String fileName = "foo.txt";
        String oldFileContents = "old content";
        String fileId = "foo";
        String fileContents = "Hello World";

        // Given there is a file with in root
        Path rootPath = Files.createTempDirectory("temp_root");
        File oldFile = rootPath.resolve(fileName).toFile();
        FileUtils.writeStringToFile(oldFile, oldFileContents);

        // And a manifest with a newer version of the file
        Tree rootTree = manifest.getRoot();
        manifest.put(new Blob(fileId, 10, DRIVE_TYPE));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, oldFile.lastModified() + 1));
        manifest.put(rootTree);

        // When performing a full sync
        account.addFile(fileId, fileContents);
        Syncer syncer = new Syncer(rootPath, accountsManager);
        syncer.fullSync(manifest);

        // Then newer version should be downloaded
        File[] files = getFilesInPath(rootPath);
        assertEquals(fileContents, FileUtils.readFileToString(files[0]));

        cleanup(rootPath);
    }

    @Test
    public void testFileExistsInBothManifestAndFileSystemAndFileSystemIsMoreRecent() throws Exception {
        String fileName = "foo.txt";
        String oldFileContents = "old content";
        String fileId = "foo";
        String fileContents = "Hello World";

        // Given there is a file with in root
        Path rootPath = Files.createTempDirectory("temp_root");
        File oldFile = rootPath.resolve(fileName).toFile();
        FileUtils.writeStringToFile(oldFile, oldFileContents);

        // And a manifest with an older version of the file
        Tree rootTree = manifest.getRoot();
        manifest.put(new Blob(fileId, 10, DRIVE_TYPE));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, 0));
        manifest.put(rootTree);

        // When performing a full sync
        account.addFile(fileId, fileContents);
        Syncer syncer = new Syncer(rootPath, accountsManager);
        syncer.fullSync(manifest);

        // Then file should not be downloaded
        File[] files = getFilesInPath(rootPath);
        assertEquals(oldFileContents, FileUtils.readFileToString(files[0]));

        cleanup(rootPath);
    }

    @Test
    public void testDownloadFileInDir() throws Exception {
        // Given there is a dir with file in manifest
        Blob bar = new Blob("bar", 10, DRIVE_TYPE);
        Tree foo = new Tree("foo", Collections.singletonList(
                new TreeItem(Entry.Type.BLOB, "bar", "bar.txt", 0)));
        manifest.put(foo);
        manifest.put(bar);
        Tree rootTree = manifest.getRoot();
        rootTree.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        manifest.put(rootTree);

        // And dir is not in root
        Path rootPath = Files.createTempDirectory("temp_root");

        // When performing a full sync
        account.addFile("bar", "Hello World");
        Syncer syncer = new Syncer(rootPath, accountsManager);
        syncer.fullSync(manifest);

        // Then dir and file should be downloaded
        File[] rootFiles = getFilesInPath(rootPath);
        assertEquals(1, rootFiles.length);
        File[] dirFiles = getFilesInPath(rootPath.resolve("foo"));
        assertEquals(1, dirFiles.length);
        assertEquals("Hello World", FileUtils.readFileToString(dirFiles[0]));
    }

    @Test
    @Ignore
    public void testDeleteFileInRootIfNotFoundInManifestAndManifestHasNewerVersion() throws Exception {
        // Given there is a a file in root
        Path rootPath = Files.createTempDirectory("temp_root");
        Path filePath = rootPath.resolve("foo.txt");
        Files.write(filePath, "Hello World".getBytes());

        // And a manifest which is newer than the file, but does not contain it
        Manifest manifestSpy = spy(manifest);
        when(manifestSpy.getUpdatedTime()).thenReturn(System.currentTimeMillis() + 10);

        // When performing a full sync
        Syncer syncer = new Syncer(rootPath, accountsManager);
        syncer.fullSync(manifestSpy);

        // Delete file from filesystem
        assertFalse(filePath.toFile().isFile());

        cleanup(rootPath);
    }

    private Manifest createMemoryManifest() {
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
