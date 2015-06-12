package omnidrive.filesystem.manifest.entry;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TreeTest {

    private String id = "tree-id";

    @Test
    public void testGetMissingItemReturnsNull() throws Exception {
        Tree tree = new Tree(id);
        assertNull(tree.getItem("foo.txt"));
    }

    @Test
    public void testGetItemByName() throws Exception {
        String name = "foo.txt";
        TreeItem item = new TreeItem(Entry.Type.BLOB, "foo", name);
        Tree tree = new Tree(id, Collections.singletonList(item));
        assertEquals(item, tree.getItem(name));
    }

    @Test
    public void testAddItem() throws Exception {
        Tree tree = new Tree(id);
        TreeItem item = new TreeItem(Entry.Type.BLOB, "foo", "foo.txt");
        tree.addItem(item);
        List<TreeItem> items = tree.getItems();
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

}