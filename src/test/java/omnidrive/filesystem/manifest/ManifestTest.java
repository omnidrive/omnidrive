package omnidrive.filesystem.manifest;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.BaseTest;
import omnidrive.stub.Account;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.nio.file.Path;

public class ManifestTest extends BaseTest {

    public static final String ACCOUNT_NAME = "my-account";

    private BaseAccount account = new Account(ACCOUNT_NAME);

    private Path root;

    private Storage storage;

    private Manifest manifest;

    @Before
    public void setUp() throws Exception {
        root = getResource(".").toPath().getParent();

        DB db = DBMaker.newMemoryDB().make();
        storage = new MapDbStorage(db);

        manifest = new Manifest(root, storage);
    }

    @Test
    public void testAddBlobPutsInStorage() throws Exception {
//        File file = getResource("hello.txt");
//        Blob blob = new Blob(file);
//
//        manifest.add(account, blob);
//        BlobMetadata metadata = storage.getBlob(blob.getId());
//        assertEquals(metadata, new BlobMetadata(file.length(), ACCOUNT_NAME));
    }

    @Test
    @Ignore
    public void testAddTreePutsMetadataInStorage() throws Exception {
//        TreeItem item = new TreeItem("id", "name");
//        Tree tree = new Tree(item);
//
//        manifest.add(account, tree);
//        TreeMetadata metadata = storage.getTree(tree.getId());
//        assertEquals(1, metadata.items.size());
//        assertEquals(item, metadata.items.get(0));
    }

    @Test
    public void testAddBlobUpdatesParentTree() throws Exception {
//        File file = getResource("hello.txt");
//        Blob blob = new Blob(file);
//        manifest.add(account, blob);
//
//        List<TreeItem> rootItems = storage.getTree(Manifest.ROOT_KEY).items;
//        assertEquals(1, rootItems.size());
//        assertEquals("hello.txt", rootItems.get(0).getName());
    }

    @Test
    @Ignore
    public void testAddBlobUpdatesParentTreeInNestedFile() throws Exception {
    }

}