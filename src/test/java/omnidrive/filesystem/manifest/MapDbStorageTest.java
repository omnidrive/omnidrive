package omnidrive.filesystem.manifest;

import omnidrive.filesystem.entry.TreeItem;
import omnidrive.filesystem.entry.TreeMetadata;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MapDbStorageTest {

    private Storage storage;

    @Before
    public void setUp() throws Exception {
        DB db = DBMaker.newMemoryDB().make();
        storage = new MapDbStorage(db);
    }

    @Test
    public void testPutAndGetEmptyTreeMetadata() throws Exception {
        String id = "foo";

        storage.put(id, new TreeMetadata());

        TreeMetadata metadata = storage.get(id);
        assertEquals(0, metadata.items.size());
    }

    @Test
    public void testPutAndGetTreeMetadataWithItems() throws Exception {
        String id = "foo";

        TreeItem item1 = new TreeItem("bar", "bar.txt");
        TreeItem item2 = new TreeItem("baz", "bar.txt");
        TreeMetadata metadata = new TreeMetadata(item1, item2);
        storage.put(id, metadata);

        List<TreeItem> result = storage.get(id).items;
        assertEquals(2, result.size());
        assertEquals(item1, result.get(0));
        assertEquals(item2, result.get(1));
    }
}