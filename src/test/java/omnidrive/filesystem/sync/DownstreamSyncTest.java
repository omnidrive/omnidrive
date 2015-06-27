package omnidrive.filesystem.sync;

import omnidrive.api.base.BaseAccount;
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

import java.io.File;
import java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DownstreamSyncTest extends BaseTest {

    @Test
    public void testDownloadOneFileInRoot() throws Exception {
        // Given a manifest with one file at root
        DB db = MapDbUtils.createMemoryDb();
        Manifest manifest = new MapDbManifest(db);
        Tree rootTree = manifest.getRoot();
        manifest.put(new Blob("foo", 10, DriveType.Dropbox));
        rootTree.addItem(new TreeItem(Entry.Type.BLOB, "foo", "foo.txt", 0));
        manifest.put(rootTree);

        // When you pull changes downstream
        File rootFile = getResource(".");
        AccountsManager accountsManager = new AccountsManager();
        BaseAccount account = new Account(0, 100);
        accountsManager.setAccount(DriveType.Dropbox, account);
        DownstreamSync downstreamSync = new DownstreamSync(rootFile.toPath(), accountsManager);
        downstreamSync.download(manifest);

        // Then file should be created at root
//        rootFile.listFiles().le
    }
}
