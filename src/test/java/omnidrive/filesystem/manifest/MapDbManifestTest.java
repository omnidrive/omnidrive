package omnidrive.filesystem.manifest;

import omnidrive.api.base.DriveType;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.util.MapDbUtils;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapDbManifestTest {

    private Manifest manifest;

    @Before
    public void setUp() throws Exception {
        DB db = MapDbUtils.createMemoryDb();
        manifest = new MapDbManifest(db);
    }

    @Test
    public void testPutAndGetEmptyTree() throws Exception {
        // When you put an empty tree in the manifest
        String id = "foo";
        manifest.put(new Tree(id));

        // Then you can get back that tree
        Tree tree = manifest.getTree(id);
        assertEquals(id, tree.getId());
        assertTrue(tree.getItems().isEmpty());
    }

    @Test
    public void testPutAndGetTreeWithItems() throws Exception {
        // When you put a non-empty tree in the manifest
        String id = "foo";
        TreeItem item1 = new TreeItem(Entry.Type.BLOB, "bar", "bar.txt");
        TreeItem item2 = new TreeItem(Entry.Type.BLOB, "baz", "bar.txt");
        Tree tree = new Tree(id, Arrays.asList(item1, item2));
        manifest.put(tree);

        // Then you can get back that tree
        List<TreeItem> result = manifest.getTree(id).getItems();
        assertEquals(2, result.size());
        assertEquals(item1, result.get(0));
        assertEquals(item2, result.get(1));
    }

    @Test
    public void testPutAndGetBlob() throws Exception {
        // When you put a blob in the manifest
        String id = "foo";
        long size = 10;
        DriveType account = DriveType.Dropbox;
        Blob blob = new Blob(id, size, account);
        manifest.put(blob);

        // Then you can get back that blob
        Blob result = manifest.getBlob(id);
        assertEquals(blob, result);
    }

    @Test
    public void testInitEmptyRootIfDbIsEmpty() throws Exception {
        // Given en empty manifest
        // When you get the root
        Tree root = manifest.getRoot();

        // Then the root has no items
        assertTrue(root.getItems().isEmpty());
    }

    @Test
    public void testUseExistingRootIfPossible() throws Exception {
        // Given a non-empty root in the manifest
        File dbFile = createTempFile();
        DB db = MapDbUtils.createFileDb(dbFile);
        manifest = new MapDbManifest(db);
        TreeItem item = new TreeItem(Entry.Type.BLOB, "foo", "foo.txt");
        Tree root = new Tree(MapDbManifest.ROOT_KEY, Collections.singletonList(item));
        manifest.put(root);

        // When you reopen the db
        db.commit();
        db.close();
        db = MapDbUtils.createFileDb(dbFile);
        manifest = new MapDbManifest(db);

        // Then the root contains the saved items
        List<TreeItem> items = manifest.getRoot().getItems();
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    private File createTempFile() throws IOException {
        return File.createTempFile("manifest", "db");
    }

}