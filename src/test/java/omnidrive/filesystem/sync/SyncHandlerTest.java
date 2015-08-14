package omnidrive.filesystem.sync;

import com.google.common.io.CharStreams;
import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.BaseTest;
import omnidrive.filesystem.exception.InvalidFileException;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.sync.ManifestSync;
import omnidrive.filesystem.manifest.sync.MapDbManifest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.filesystem.sync.upload.SimpleUploadStrategy;
import omnidrive.filesystem.sync.upload.UploadStrategy;
import omnidrive.filesystem.sync.upload.Uploader;
import omnidrive.util.MapDbUtils;
import org.junit.Before;
import org.junit.Test;
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

    public static final AccountType DRIVE_TYPE = AccountType.Dropbox;

    public static final String UPLOAD_ID = "new-id";

    private Manifest manifest = new MapDbManifest(MapDbUtils.createMemoryDb());

    private AccountsManager accountsManager = new AccountsManager();

    private ManifestSync manifestSync = mock(ManifestSync.class);

    private CloudAccount account = mock(CloudAccount.class);

    private SyncHandler handler;

    @Before
    public void setUp() throws Exception {
        accountsManager.setAccount(DRIVE_TYPE, account);
        UploadStrategy uploadStrategy = new SimpleUploadStrategy(accountsManager);
        Uploader uploader = new Uploader(uploadStrategy);
        handler = new SyncHandler(getRoot(), manifest, manifestSync, uploader, accountsManager);

        when(account.getType()).thenReturn(DRIVE_TYPE);
        when(account.uploadFile(anyString(), any(InputStream.class), anyLong())).thenReturn(UPLOAD_ID);
        when(account.getQuotaRemainingSize()).thenReturn(100L);
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
        Blob actual = manifest.get(id, Blob.class);
        assertEquals(expected, actual);
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
        manifest.put(new Tree("foo", Collections.singletonList(new TreeItem(Entry.Type.TREE, "bar", "bar", 0))));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        manifest.put(root);

        // When a file is created in nested dir
        File file = getResource("foo/bar/baz.txt");
        handler.create(file);

        // Then an entry is added to parent tree
        List<TreeItem> items = manifest.get("bar", Tree.class).getItems();
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
        assertEquals(expected, manifest.get(id, Tree.class));
    }

    @Test
    public void testCreateDirAddsEntryInParentTree() throws Exception {
        // Given the manifest contains a dir foo
        manifest.put(new Tree("foo"));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        manifest.put(root);

        // When another dir is created in foo
        File file = getResource("foo/bar");
        handler.create(file);

        // Then an entry is added to parent tree
        List<TreeItem> items = manifest.get("foo", Tree.class).getItems();
        assertEquals(1, items.size());
        assertEquals("bar", items.get(0).getName());
    }

    @Test
    public void testCreateFileSyncsManifest() throws Exception {
        // When a file is created
        File file = getResource("hello.txt");
        handler.create(file);

        // Then the manifest is synced to all accounts
        verify(manifestSync).uploadToAll(accountsManager.getActiveAccounts());
    }

    @Test
    public void testCreateDirSyncsManifest() throws Exception {
        // When a dir is created
        File dir = getResource("foo");
        handler.create(dir);

        // Then the manifest is synced to all accounts
        verify(manifestSync).uploadToAll(accountsManager.getActiveAccounts());
    }

    @Test
    public void testModifyFileSyncsManifest() throws Exception {
        // Given a file exists in the manifest
        String id = UPLOAD_ID;
        String filename = "modify.txt";
        manifest.put(new Blob(id, 5L, DRIVE_TYPE));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename, 0));
        manifest.put(root);

        // When this file is modified
        File file = getResource(filename);
        writeToFile(file, "Hello World");
        handler.modify(file);

        // Then the manifest is synced to all accounts
        verify(manifestSync).uploadToAll(accountsManager.getActiveAccounts());
    }

    @Test
    public void testDeleteFileSyncsManifest() throws Exception {
        // Given a file exists in the manifest
        String id = UPLOAD_ID;
        String filename = "delete.txt";
        manifest.put(new Blob(id, 5L, DRIVE_TYPE));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename, 0));
        manifest.put(root);

        // When this file is deleted
        File file = getResource(filename);
        handler.delete(file);

        // Then the manifest is synced to all accounts
        verify(manifestSync).uploadToAll(accountsManager.getActiveAccounts());
    }

    @Test
    public void testDeleteDirSyncsManifest() throws Exception {
        // Given a file exists in the manifest
        String id = UPLOAD_ID;
        String filename = "foo";
        manifest.put(new Tree(id));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, id, filename, 0));
        manifest.put(root);

        // When this file is deleted
        File file = getResource(filename);
        handler.delete(file);

        // Then the manifest is synced to all accounts
        verify(manifestSync).uploadToAll(accountsManager.getActiveAccounts());
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
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename, 0));
        manifest.put(root);

        // When this file is modified
        File file = getResource(filename);
        writeToFile(file, newContents);
        String modifyId = handler.modify(file);

        // Then the blob in the manifest is updated
        assertEquals(id, modifyId);
        assertEquals(newSize, manifest.get(id, Blob.class).getSize());
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
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename, 0));
        manifest.put(root);

        // When this file is modified
        File file = getResource(filename);
        writeToFile(file, newContents);
        handler.modify(file);

        // And the file needs to be updated in the account
        ArgumentCaptor<InputStream> argument = ArgumentCaptor.forClass(InputStream.class);
        verify(account).updateFile(eq(id), argument.capture(), eq(newSize));
        InputStream value = argument.getValue();
        assertEquals(newContents, inputStreamToString(value));
    }

    @Test
    public void testDeleteFileRemovesFromManifest() throws Exception {
        // Given a file is in the manifest
        String id = UPLOAD_ID;
        String filename = "delete.txt";
        manifest.put(new Blob(id, 10L, DRIVE_TYPE));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename, 0));
        manifest.put(root);

        // When this file is deleted
        File file = getResource(filename);
        handler.delete(file);

        // Then it should be removed from the manifest
        assertNull(manifest.get(id, Blob.class));
    }

    @Test
    public void testDeleteFileFromRootRemovesEntryInParentTree() throws Exception {
        // Given two files are in the root
        String id = UPLOAD_ID;
        String otherId = "other";
        String filename = "delete.txt";
        manifest.put(new Blob(id, 10L, DRIVE_TYPE));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename, 0));
        root.addItem(new TreeItem(Entry.Type.BLOB, otherId, "file", 0));
        manifest.put(root);

        // When one of these files is deleted
        File file = getResource(filename);
        handler.delete(file);

        // Then its entry is deleted from root
        List<TreeItem> items = manifest.getRoot().getItems();
        assertEquals(1, items.size());
        assertEquals(otherId, items.get(0).getId());
    }

    @Test
    public void testDeleteFileDeletesFromAccount() throws Exception {
        // Given a file is in the manifest
        String id = UPLOAD_ID;
        String filename = "delete.txt";
        manifest.put(new Blob(id, 10L, DRIVE_TYPE));
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.BLOB, id, filename, 0));
        manifest.put(root);

        // When one of these files is deleted
        File file = getResource(filename);
        handler.delete(file);

        // Then it's deleted from account
        verify(account).removeFile(eq(id));
    }

    @Test
    public void testDeleteDirRemoveEntryInParentTree() throws Exception {
        // Given some nested files and dirs exist in the manifest
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        root.addItem(new TreeItem(Entry.Type.BLOB, "hello", "hello.txt", 0));
        Tree foo = new Tree("foo");
        Blob hello = new Blob("hello", 10L, AccountType.Dropbox);
        manifest.put(root);
        manifest.put(foo);
        manifest.put(hello);

        // When the parent dir is deleted
        File dir = getResource("foo");
        handler.delete(dir);

        // Then all its contents is removed from the manifest recursively
        List<TreeItem> items = manifest.getRoot().getItems();
        assertEquals(1, items.size());
        assertEquals("hello", items.get(0).getId());
    }

    @Test
    public void testDeleteDirRemovesItsContentsRecursively() throws Exception {
        // Given some nested files and dirs exist in the manifest
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        root.addItem(new TreeItem(Entry.Type.BLOB, "hello", "hello.txt", 0));
        Tree foo = new Tree("foo");
        foo.addItem(new TreeItem(Entry.Type.TREE, "bar", "bar", 0));
        Tree bar = new Tree("bar");
        bar.addItem(new TreeItem(Entry.Type.BLOB, "baz", "baz.txt", 0));
        Blob baz = new Blob("baz", 5L, DRIVE_TYPE);
        Blob hello = new Blob("hello", 10L, DRIVE_TYPE);
        manifest.put(root);
        manifest.put(foo);
        manifest.put(bar);
        manifest.put(baz);
        manifest.put(hello);

        // When the parent dir is deleted
        File dir = getResource("foo");
        handler.delete(dir);

        // Then all its contents is removed recursively
        assertNull(manifest.get("foo", Tree.class));
        assertNull(manifest.get("bar", Tree.class));
        assertNull(manifest.get("baz", Blob.class));
        verify(account).removeFile(eq("baz"));
    }

    @Test(expected = InvalidFileException.class)
    public void testDeleteInvalidFileThrowsException() throws Exception {
        File file = new File("foo");
        handler.delete(file);
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