package omnidrive.filesystem.manifest;

import omnidrive.api.base.AccountType;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.filesystem.manifest.mapdb.MapDbManifest;
import omnidrive.util.MapDbUtils;
import org.junit.Test;
import org.mapdb.DB;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapDbManifestTest {

    private MapDbManifest manifest = getManifest();

//    @Test
//    public void testGetAuthTokensReturnsEmptyMap() throws Exception {
//        // Given no auth tokens were registered
//
//        // When you request auth tokens
//        Map<AccountType, AccountMetadata> accountsMetadata = manifest.getAccountsMetadata();
//
//        // Then you gen an empty list
//        assertTrue(accountsMetadata.isEmpty());
//    }

//    @Test
//    public void testGetAuthTokensIfExist() throws Exception {
//        // Given an auth token was registered
//        AccountMetadata metadata = new AccountMetadata("foo", "bar");
//        AccountType accountType = AccountType.Dropbox;
//        manifest.put(accountType, metadata);
//
//        // When you request auth tokens
//        Map<AccountType, AccountMetadata> accountsMetada = manifest.getAccountsMetadata();
//
//        // Then you get the auth token
//        assertEquals(metadata, accountsMetada.get(accountType));
//    }

    @Test
    public void testUpdateTimeOnPut() throws Exception {
        // Given two manifests exist
        MapDbManifest manifest1 = getManifest();
        MapDbManifest manifest2 = getManifest();

        // When one is updated after the other
        Blob blob = new Blob("foo", 5L, AccountType.Dropbox);
        long sleepTime = 10L;
        manifest1.put(blob);
        Thread.sleep(sleepTime);
        manifest2.put(blob);

        // Then the update time should be greater
        assertTrue(manifest1.getUpdatedTime() < manifest2.getUpdatedTime());

        // And vice versa
        Thread.sleep(sleepTime);
        manifest1.put(blob);
        assertTrue(manifest1.getUpdatedTime() > manifest2.getUpdatedTime());
    }

    @Test
    public void testUpdateTimeOnRemove() throws Exception {
        // Given two manifests exist entries
        MapDbManifest manifest1 = getManifest();
        MapDbManifest manifest2 = getManifest();
        Blob blob1 = new Blob("foo", 5L, AccountType.Dropbox);
        Blob blob2 = new Blob("bar", 5L, AccountType.Dropbox);
        manifest1.put(blob1);
        manifest1.put(blob2);
        manifest2.put(blob1);

        // When one is updated after the other
        manifest1.remove(blob1);
        long sleepTime = 10L;
        Thread.sleep(sleepTime);
        manifest2.remove(blob1);

        // Then the update time should be greater
        assertTrue(manifest1.getUpdatedTime() < manifest2.getUpdatedTime());

        // And vice versa
        Thread.sleep(sleepTime);
        manifest1.remove(blob2);
        assertTrue(manifest1.getUpdatedTime() > manifest2.getUpdatedTime());
    }

    @Test
    public void testPutAndGetEmptyTree() throws Exception {
        // When you put an empty tree in the manifest
        String id = "foo";
        manifest.put(new Tree(id));

        // Then you can get back that tree
        Tree tree = manifest.get(id, Tree.class);
        assertEquals(id, tree.getId());
        assertTrue(tree.getItems().isEmpty());
    }

    @Test
    public void testPutAndGetTreeWithItems() throws Exception {
        // When you put a non-empty tree in the manifest
        String id = "foo";
        TreeItem item1 = new TreeItem(Entry.Type.BLOB, "bar", "bar.txt", 0);
        TreeItem item2 = new TreeItem(Entry.Type.BLOB, "baz", "bar.txt", 0);
        Tree tree = new Tree(id, Arrays.asList(item1, item2));
        manifest.put(tree);

        // Then you can get back that tree
        List<TreeItem> result = manifest.get(id, Tree.class).getItems();
        assertEquals(2, result.size());
        assertEquals(item1, result.get(0));
        assertEquals(item2, result.get(1));
    }

    @Test
    public void testPutAndGetBlob() throws Exception {
        // When you put a blob in the manifest
        String id = "foo";
        long size = 10L;
        AccountType account = AccountType.Dropbox;
        Blob blob = new Blob(id, size, account);
        manifest.put(blob);

        // Then you can get back that blob
        Blob result = manifest.get(id, Blob.class);
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
        TreeItem item = new TreeItem(Entry.Type.BLOB, "foo", "foo.txt", 0);
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

    @Test
    public void testRemoveBlob() throws Exception {
        // Given a blob is in the manifest
        Blob blob = new Blob("foo", 10L, AccountType.Dropbox);
        manifest.put(blob);

        // When you call remove
        manifest.remove(blob);

        // Then it should be removed
        assertNull(manifest.get(blob.getId(), Blob.class));
    }

    @Test
    public void testRemoveTree() throws Exception {
        // Given a tree is in the manifest
        Tree tree = new Tree("foo");
        manifest.put(tree);

        // When you call remove
        manifest.remove(tree);

        // Then it should be removed
        assertNull(manifest.get(tree.getId(), Tree.class));
    }

    private MapDbManifest getManifest() {
        DB db1 = MapDbUtils.createMemoryDb();
        return new MapDbManifest(db1);
    }

    private File createTempFile() throws IOException {
        return File.createTempFile("manifest", "db");
    }

}