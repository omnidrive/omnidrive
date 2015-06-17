package omnidrive.filesystem.sync;

import com.google.common.io.CharStreams;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.BaseTest;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SyncHandlerTest extends BaseTest {

    public static final String ACCOUNT_NAME = "my-account";

    public static final String UPLOAD_ID = "new-id";

    private Manifest manifest;

    private BaseAccount account;

    private SyncHandler handler;

    @Before
    public void setUp() throws Exception {
        manifest = createManifest();
        UploadStrategy uploadStrategy = mock(UploadStrategy.class);
        AccountsManager accountsManager = mock(AccountsManager.class);
        handler = new SyncHandler(getRoot(), manifest, uploadStrategy, accountsManager);

        account = mock(BaseAccount.class);
        when(uploadStrategy.selectAccount()).thenReturn(account);
        when(account.getName()).thenReturn(ACCOUNT_NAME);
        when(account.uploadFile(anyString(), any(InputStream.class), anyLong())).thenReturn(UPLOAD_ID);
        when(accountsManager.getActiveAccounts()).thenReturn(Collections.singletonList(account));
    }

    @After
    public void tearDown() throws Exception {
        manifest.close();

    }

    @Test
    public void testCreateFileUploadsToAccountUsingStrategy() throws Exception {
        File file = getResource("hello.txt");
        ArgumentCaptor<String> nameArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> inputStreamArgument = ArgumentCaptor.forClass(InputStream.class);

        handler.create(file);

        verify(account).uploadFile(nameArgument.capture(), inputStreamArgument.capture(), eq(file.length()));
        assertValidUUID(nameArgument.getValue());
        assertEquals("Hello World", toString(inputStreamArgument.getValue()));
    }

    @Test
    public void testCreateFileAddsBlobToManifest() throws Exception {
        File file = getResource("hello.txt");

        String id = handler.create(file);

        assertEquals(UPLOAD_ID, id);
        Blob expected = new Blob(id, file.length(), account.getName());
        assertEquals(expected, manifest.getBlob(id));
    }

    @Test
    public void testCreateFileSyncsManifest() throws Exception {
        File file = getResource("hello.txt");

        handler.create(file);

        verify(account).uploadFile(eq("manifest"), any(InputStream.class), anyLong());
    }

    @Test
    public void testCreateBlobAddsEntryInParentTree() throws Exception {
        Tree root = manifest.getRoot();
        assertTrue(root.getItems().isEmpty());

        String name = "hello.txt";
        File file = getResource(name);
        handler.create(file);

        List<TreeItem> items = manifest.getRoot().getItems();
        assertEquals(1, items.size());
        assertEquals(name, items.get(0).getName());
    }

    @Test
    public void testCreateBlobInSubDirAddsEntryInParentTree() throws Exception {
        manifest.put(new Tree("bar"));
        manifest.put(new Tree("foo", Collections.singletonList(new TreeItem(Entry.Type.TREE, "bar", "bar"))));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo"));
        manifest.put(root);

        File file = getResource("foo/bar/baz.txt");
        handler.create(file);

        List<TreeItem> items = manifest.getTree("bar").getItems();
        assertEquals(1, items.size());
        assertEquals("baz.txt", items.get(0).getName());
    }

    @Test
    public void testCreateEmptyDirAddsTreeToManifest() throws Exception {
        File dir = getResource("foo");

        String id = handler.create(dir);

        assertValidUUID(id);
        Tree expected = new Tree(id);
        assertEquals(expected, manifest.getTree(id));
    }

    @Test
    public void testCreateDirAddsEntryInParentTree() throws Exception {
        manifest.put(new Tree("foo"));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo"));
        manifest.put(root);

        File file = getResource("foo/bar");
        handler.create(file);

        List<TreeItem> items = manifest.getTree("foo").getItems();
        assertEquals(1, items.size());
        assertEquals("bar", items.get(0).getName());
    }

    private Manifest createManifest() throws IOException {
        File manifestFile = File.createTempFile("manifest", ".tmp");
        return new MapDbManifest(manifestFile);
    }

    private void assertValidUUID(String id) {
        try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(id);
        } catch (Exception e) {
            fail("Invalid UUID: " + id);
        }
    }

    private String toString(InputStream value) throws IOException {
        return CharStreams.toString(new InputStreamReader(value));
    }

    private Path getRoot() {
        try {
            return getResource(".").toPath();
        } catch (URISyntaxException e) {
            return null;
        }
    }

}