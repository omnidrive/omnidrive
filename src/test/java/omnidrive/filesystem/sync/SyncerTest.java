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
import org.junit.Test;
import org.mapdb.DB;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class SyncerTest extends BaseTest {

    @Test
    public void testFullSyncDownloadOneFileIfNotExistsInRoot() throws Exception {
        // Given a manifest with one file at root
        DB db = MapDbUtils.createMemoryDb();
        Manifest manifest = new MapDbManifest(db);
        Tree rootTree = manifest.getRoot();
        String fileId = "foo";
        String fileContents = "Hello World";
        String fileName = "foo.txt";
        manifest.put(new Blob(fileId, 10, DriveType.Dropbox));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, 0));
        manifest.put(rootTree);

        // And file is not in root
        Path rootPath = Files.createTempDirectory("temp_root");

        // When performing a full sync
        AccountsManager accountsManager = new AccountsManager();
        Account account = new Account(100);
        account.addFile(fileId, fileContents);
        accountsManager.setAccount(DriveType.Dropbox, account);
        Syncer syncer = new Syncer(rootPath, account);
        syncer.fullSync(manifest);

        // Then file should be downloaded
        File root = rootPath.toFile();
        File[] files = root.listFiles();
        assert files != null;
        File file = files[0];
        assertEquals(1, files.length);
        assertEquals(fileName, file.getName());
        assertEquals(fileContents, FileUtils.readFileToString(file));

        FileUtils.deleteDirectory(root);
    }

    @Test
    public void testFullSyncDownloadFileIfManifestHasNewerVersion() throws Exception {
        // Given there is a file with in root
        String fileName = "foo.txt";
        String oldFileContents = "old content";
        Path rootPath = Files.createTempDirectory("temp_root");
        File oldFile = rootPath.resolve(fileName).toFile();
        FileUtils.writeStringToFile(oldFile, oldFileContents);

        // And a manifest with a newer version of the file
        DB db = MapDbUtils.createMemoryDb();
        Manifest manifest = new MapDbManifest(db);
        Tree rootTree = manifest.getRoot();
        String fileId = "foo";
        String fileContents = "Hello World";
        manifest.put(new Blob(fileId, 10, DriveType.Dropbox));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, oldFile.lastModified() + 1));
        manifest.put(rootTree);

        // When performing a full sync
        AccountsManager accountsManager = new AccountsManager();
        Account account = new Account(100);
        account.addFile(fileId, fileContents);
        accountsManager.setAccount(DriveType.Dropbox, account);
        Syncer syncer = new Syncer(rootPath, account);
        syncer.fullSync(manifest);

        // Then newer version should be downloaded
        File root = rootPath.toFile();
        File[] files = root.listFiles();
        assert files != null;
        assertEquals(fileContents, FileUtils.readFileToString(files[0]));

        FileUtils.deleteDirectory(root);
    }

    @Test
    public void testFullSyncDoesNotDownloadFileIfManifestHasOlderVersion() throws Exception {
        // Given there is a file with in root
        String fileName = "foo.txt";
        String oldFileContents = "old content";
        Path rootPath = Files.createTempDirectory("temp_root");
        File oldFile = rootPath.resolve(fileName).toFile();
        FileUtils.writeStringToFile(oldFile, oldFileContents);

        // And a manifest with an older version of the file
        DB db = MapDbUtils.createMemoryDb();
        Manifest manifest = new MapDbManifest(db);
        Tree rootTree = manifest.getRoot();
        String fileId = "foo";
        String fileContents = "Hello World";
        manifest.put(new Blob(fileId, 10, DriveType.Dropbox));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, fileId, fileName, 0));
        manifest.put(rootTree);

        // When performing a full sync
        AccountsManager accountsManager = new AccountsManager();
        Account account = new Account(100);
        account.addFile(fileId, fileContents);
        accountsManager.setAccount(DriveType.Dropbox, account);
        Syncer syncer = new Syncer(rootPath, account);
        syncer.fullSync(manifest);

        // Then newer version should not be downloaded
        File root = rootPath.toFile();
        File[] files = root.listFiles();
        assert files != null;
        assertEquals(oldFileContents, FileUtils.readFileToString(files[0]));

        FileUtils.deleteDirectory(root);
    }

}
