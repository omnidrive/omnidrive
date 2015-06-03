package omnidrive.filesystem.manifest;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.BaseTest;
import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.entry.BlobMetadata;
import omnidrive.filesystem.entry.TreeItem;
import omnidrive.filesystem.entry.TreeMetadata;
import omnidrive.stub.Account;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ManifestTest extends BaseTest {

    public static final String ACCOUNT_NAME = "my-account";

    private BaseAccount account = new Account(ACCOUNT_NAME);

    private Storage storage = mock(Storage.class);

    private Path root;

    private Manifest manifest;

    @Before
    public void setUp() throws URISyntaxException {
        root = getResource(".").toPath().getParent();
        manifest = new Manifest(root, storage);
    }

    @Test
    public void testAddBlobPutsMetadataInStorage() throws Exception {
        String id = "some id";
        String content = "Hello World";
        Blob blob = new Blob(id, new ByteArrayInputStream(content.getBytes()), content.length());

        manifest.add(account, blob);
        verify(storage).put(id, new BlobMetadata(11, ACCOUNT_NAME));
    }

    @Test
    public void testAddBlobUpdatesParentTree() throws Exception {
        DB db = DBMaker.newMemoryDB().make();
        Storage storage = new MapDbStorage(db);
        Manifest manifest = new Manifest(root, storage);

        // Init empty root
        storage.put(Manifest.ROOT_KEY, new TreeMetadata());

        // Add blob
        File file = getResource("hello.txt");
        Blob blob = new Blob(file);
        manifest.add(account, blob);

        List<TreeItem> rootItems = storage.get(Manifest.ROOT_KEY).items;
        assertEquals(1, rootItems.size());
        assertEquals("hello.txt", rootItems.get(0).getName());
    }

}