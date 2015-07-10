package omnidrive.filesystem.sync;

import com.google.common.io.CharStreams;
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
import org.junit.Test;
import org.mapdb.DB;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class SyncerTest extends BaseTest {

    @Test
    public void testFullSyncDownloadOneFileInRoot() throws Exception {
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

        // When performing a full sync
        Path rootPath = Files.createTempDirectory("temp_root");
        AccountsManager accountsManager = new AccountsManager();
        Account account = new Account(100);
        account.addFile(fileId, fileContents);
        accountsManager.setAccount(DriveType.Dropbox, account);
        Syncer syncer = new Syncer(rootPath, account);
        syncer.fullSync(manifest);

        // Then file should be downloaded to root
        File[] files = rootPath.toFile().listFiles();
        assert files != null;
        File file = files[0];
        assertEquals(1, files.length);
        assertEquals(fileName, file.getName());
        assertEquals(fileContents, getContents(file));

        // TODO: delete temp dir
    }

    private String getContents(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        Reader reader = new InputStreamReader(inputStream);
        return CharStreams.toString(reader);
    }

}
