package omnidrive.filesystem.manifest;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.BaseTest;
import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.entry.BlobMetadata;
import omnidrive.filesystem.entry.TreeItem;
import omnidrive.stub.Account;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ManifestTest extends BaseTest {

    public static final String ACCOUNT_NAME = "my-account";

    private BaseAccount account = new Account(ACCOUNT_NAME);

    private Path root;

    private Storage storage;

    private Manifest manifest;

    @Before
    public void setUp() throws URISyntaxException {
        root = getResource(".").toPath().getParent();

        DB db = DBMaker.newMemoryDB().make();
        storage = new MapDbStorage(db);

        manifest = new Manifest(root, storage);
    }

    @Test
    public void testAddFilePutsMetadataInStorage() throws Exception {
        File file = getResource("hello.txt");
        Blob blob = new Blob(file);

        manifest.add(account, blob);
        BlobMetadata metadata = storage.getBlobMetadata(blob.getId());
        assertEquals(metadata, new BlobMetadata(file.length(), ACCOUNT_NAME));
    }

    @Test
    public void testAddBlobUpdatesParentTree() throws Exception {
        File file = getResource("hello.txt");
        Blob blob = new Blob(file);
        manifest.add(account, blob);

        List<TreeItem> rootItems = storage.getTreeMetadata(Manifest.ROOT_KEY).items;
        assertEquals(1, rootItems.size());
        assertEquals("hello.txt", rootItems.get(0).getName());
    }

}