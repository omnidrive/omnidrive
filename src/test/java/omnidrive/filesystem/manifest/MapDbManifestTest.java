package omnidrive.filesystem.manifest;

import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapDbManifestTest {

    private Manifest manifest;

    @Before
    public void setUp() throws Exception {
        DB db = DBMaker.newMemoryDB().make();
        manifest = new MapDbManifest(db);
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
        TreeItem item1 = new TreeItem("bar", "bar.txt");
        TreeItem item2 = new TreeItem("baz", "bar.txt");
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
        String account = "my-account";
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
        // Temp DB file
        File file = File.createTempFile("manifest", "db");
        DB db;

        // Init non-empty root
        db = DBMaker.newFileDB(file).closeOnJvmShutdown().make();
        manifest = new MapDbManifest(db);
        TreeItem item = new TreeItem("foo", "foo.txt");
        Tree root = new Tree(MapDbManifest.ROOT_KEY, Collections.singletonList(item));
        manifest.put(root);

        // Close DB
        db.commit();
        db.close();

        // Reopen DB to find root
        db = DBMaker.newFileDB(file).closeOnJvmShutdown().make();
        manifest = new MapDbManifest(db);
        List<TreeItem> items = manifest.getRoot().getItems();
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));

        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

}