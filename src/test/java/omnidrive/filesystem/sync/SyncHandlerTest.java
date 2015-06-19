package omnidrive.filesystem.sync;

import com.google.common.io.CharStreams;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.BaseTest;
import omnidrive.filesystem.exception.InvalidFileException;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.ManifestSync;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.util.MapDbUtils;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;
import org.mockito.ArgumentCaptor;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SyncHandlerTest extends BaseTest {

    public static final DriveType DRIVE_TYPE = DriveType.Dropbox;

    public static final String UPLOAD_ID = "new-id";

    private Manifest manifest;

    private ManifestSync manifestSync;

    private BaseAccount account;

    private SyncHandler handler;

    @Before
    public void setUp() throws Exception {
        DB db = MapDbUtils.createMemoryDb();
        manifest = new MapDbManifest(db);
        manifestSync = mock(ManifestSync.class);
        UploadStrategy uploadStrategy = mock(UploadStrategy.class);
        AccountsManager accountsManager = mock(AccountsManager.class);
        handler = new SyncHandler(getRoot(), manifest, manifestSync, uploadStrategy, accountsManager);
        account = mock(BaseAccount.class);

        when(accountsManager.getAccount(DRIVE_TYPE)).thenReturn(account);
        when(accountsManager.toType(account)).thenReturn(DRIVE_TYPE);
        when(uploadStrategy.selectAccount()).thenReturn(account);
        when(account.uploadFile(anyString(), any(InputStream.class), anyLong())).thenReturn(UPLOAD_ID);
        when(accountsManager.getActiveAccounts()).thenReturn(Collections.singletonList(account));
    }

    @Test(expected = InvalidFileException.class)
    public void testCreateFileWhichDoesNotExistThrowsException() throws Exception {
        File file = new File("foo");
        handler.create(file);
    }

    @Test
    public void testCreateFileUploadsToAccountUsingStrategy() throws Exception {
        // When a file is created
        File file = getResource("hello.txt");
        handler.create(file);

        // Then file is uploaded to account chosen by strategy
        ArgumentCaptor<String> nameArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> inputStreamArgument = ArgumentCaptor.forClass(InputStream.class);
        verify(account).uploadFile(nameArgument.capture(), inputStreamArgument.capture(), eq(file.length()));
        assertValidUUID(nameArgument.getValue());
        assertEquals("Hello World", inputStreamToString(inputStreamArgument.getValue()));
    }

    @Test
    public void testCreateFileAddsBlobToManifest() throws Exception {
        // When a file is created
        File file = getResource("hello.txt");
        String id = handler.create(file);

        // Then a blob is added to the manifest
        assertEquals(UPLOAD_ID, id);
        Blob expected = new Blob(id, file.length(), DRIVE_TYPE);
        assertEquals(expected, manifest.getBlob(id));
    }

    @Test
    public void testCreateBlobAddsEntryInParentTree() throws Exception {
        // Given that the root of the manifest is empty
        Tree root = manifest.getRoot();
        assertTrue(root.getItems().isEmpty());

        // When a file is created
        String name = "hello.txt";
        File file = getResource(name);
        handler.create(file);

        // Then an entry is added to the root
        List<TreeItem> items = manifest.getRoot().getItems();
        assertEquals(1, items.size());
        assertEquals(name, items.get(0).getName());
    }

    @Test
    public void testCreateBlobInSubDirAddsEntryInParentTree() throws Exception {
        // Given the manifest contains nested dir foo/bar
        manifest.put(new Tree("bar"));
        manifest.put(new Tree("foo", Collections.singletonList(new TreeItem(Entry.Type.TREE, "bar", "bar"))));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo"));
        manifest.put(root);

        // When a file is created in nested dir
        File file = getResource("foo/bar/baz.txt");
        handler.create(file);

        // Then an entry is added to parent tree
        List<TreeItem> items = manifest.getTree("bar").getItems();
        assertEquals(1, items.size());
        assertEquals("baz.txt", items.get(0).getName());
    }

    @Test
    public void testCreateEmptyDirAddsTreeToManifest() throws Exception {
        // When a dir is created
        File dir = getResource("foo");
        String id = handler.create(dir);

        // Then an empty tree is added to the manifest
        assertValidUUID(id);
        Tree expected = new Tree(id);
        assertEquals(expected, manifest.getTree(id));
    }

    @Test
    public void testCreateDirAddsEntryInParentTree() throws Exception {
        // Given the manifest contains a dir foo
        manifest.put(new Tree("foo"));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo"));
        manifest.put(root);

        // When another dir is created in foo
        File file = getResource("foo/bar");
        handler.create(file);

        // Then an entry is added to parent tree
        List<TreeItem> items = manifest.getTree("foo").getItems();
        assertEquals(1, items.size());
        assertEquals("bar", items.get(0).getName());
    }

    @Test
    public void testCreateFileSyncsManifest() throws Exception {
        // When a file is created
        File file = getResource("hello.txt");
        handler.create(file);

        // Then the manifest is synced to all accounts
        verify(manifestSync).upload();
    }

    @Test
    public void testCreateDirSyncsManifest() throws Exception {
        // When a dir is created
        File dir = getResource("foo");
        handler.create(dir);

        // Then the manifest is synced to all accounts
        verify(manifestSync).upload();
    }

    @Test
    public void testModifyFileSyncsManifest() throws Exception {
        // Given a file exists in the manifest
        String id = UPLOAD_ID;
        String filename = "modify.txt";
        manifest.put(new Blob(id, 5L, DRIVE_TYPE));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename));
        manifest.put(root);

        // When this file is modified
        File file = getResource(filename);
        writeToFile(file, "Hello World");
        handler.modify(file);

        // Then the manifest is synced to all accounts
        verify(manifestSync).upload();
    }

    @Test(expected = InvalidFileException.class)
    public void testModifyInvalidFileThrowsException() throws Exception {
        File file = new File("foo");
        handler.modify(file);
    }

    @Test
    public void testModifyFileUpdatesBlobWithNewSize() throws Exception {
        // Given a file exists in the manifest
        String id = UPLOAD_ID;
        String filename = "modify.txt";
        String originalContents = "Hello";
        String newContents = "Hello World";
        long originalSize = originalContents.length();
        long newSize = newContents.length();
        manifest.put(new Blob(id, originalSize, DRIVE_TYPE));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename));
        manifest.put(root);

        // When this file is modified
        File file = getResource(filename);
        writeToFile(file, newContents);
        String modifyId = handler.modify(file);

        // Then the blob in the manifest is updated
        assertEquals(id, modifyId);
        assertEquals(newSize, manifest.getBlob(id).getSize());
    }

    @Test
    public void testModifyFileReUploadsToAccount() throws Exception {
        // Given a file exists in the manifest
        String id = UPLOAD_ID;
        String filename = "modify.txt";
        String originalContents = "Hello";
        String newContents = "Hello World";
        long originalSize = originalContents.length();
        long newSize = newContents.length();
        manifest.put(new Blob(id, originalSize, DRIVE_TYPE));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename));
        manifest.put(root);

        // When this file is modified
        File file = getResource(filename);
        writeToFile(file, newContents);
        handler.modify(file);

        // Then original file needs to be deleted from the account
        verify(account).deleteFile(eq(id));

        // And the updated file needs to be uploaded to the account
        ArgumentCaptor<InputStream> argument = ArgumentCaptor.forClass(InputStream.class);
        verify(account).uploadFile(eq(id), argument.capture(), eq(newSize));
        InputStream value = argument.getValue();
        assertEquals(newContents, inputStreamToString(value));
    }

    private void writeToFile(File file, String text) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(text.getBytes());
        outputStream.close();
    }

    private void assertValidUUID(String id) {
        try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(id);
        } catch (Exception e) {
            fail("Invalid UUID: " + id);
        }
    }

    private String inputStreamToString(InputStream value) throws IOException {
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