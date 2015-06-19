package omnidrive.filesystem.manifest;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.DriveType;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MapDbManifestTest {

    private File manifestFile;

    private Manifest manifest;

    @Before
    public void setUp() throws Exception {
        manifestFile = createTempFile();
        manifest = new MapDbManifest(manifestFile);
    }

    @After
    public void tearDown() throws Exception {
        manifest.close();
        //noinspection ResultOfMethodCallIgnored
        manifestFile.delete();
    }

    @Test
    public void testPutAndGetEmptyTree() throws Exception {
        String id = "foo";

        manifest.put(new Tree(id));

        Tree tree = manifest.getTree(id);
        assertEquals(id, tree.getId());
        assertTrue(tree.getItems().isEmpty());
    }

    @Test
    public void testPutAndGetTreeWithItems() throws Exception {
        String id = "foo";
        TreeItem item1 = new TreeItem(Entry.Type.BLOB, "bar", "bar.txt");
        TreeItem item2 = new TreeItem(Entry.Type.BLOB, "baz", "bar.txt");
        Tree tree = new Tree(id, Arrays.asList(item1, item2));

        manifest.put(tree);

        List<TreeItem> result = manifest.getTree(id).getItems();
        assertEquals(2, result.size());
        assertEquals(item1, result.get(0));
        assertEquals(item2, result.get(1));
    }

    @Test
    public void testPutAndGetBlob() throws Exception {
        String id = "foo";
        long size = 10;
        DriveType account = DriveType.Dropbox;
        Blob blob = new Blob(id, size, account);

        manifest.put(blob);

        Blob result = manifest.getBlob(id);
        assertEquals(blob, result);
    }

    @Test
    public void testInitEmptyRootIfDbIsEmpty() throws Exception {
        Tree root = manifest.getRoot();
        assertTrue(root.getItems().isEmpty());
    }

    @Test
    public void testUseExistingRootIfPossible() throws Exception {
        // Init non-empty root
        TreeItem item = new TreeItem(Entry.Type.BLOB, "foo", "foo.txt");
        Tree root = new Tree(MapDbManifest.ROOT_KEY, Collections.singletonList(item));
        manifest.put(root);

        // Close DB
        manifest.close();

        // Reopen DB to find root
        manifest = new MapDbManifest(manifestFile);
        List<TreeItem> items = manifest.getRoot().getItems();
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    public void testSyncManifestToAccounts() throws Exception {
        BaseAccount account = mock(BaseAccount.class);
        List<BaseAccount> accounts = Collections.singletonList(account);

        manifest.sync(accounts);

        verify(account).uploadFile(eq("manifest"), any(InputStream.class), anyLong());
    }

    private File createTempFile() throws IOException {
        return File.createTempFile("manifest", "db");
    }

}