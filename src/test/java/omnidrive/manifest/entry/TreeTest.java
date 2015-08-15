package omnidrive.manifest.entry;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TreeTest {

    private String id = "tree-id";

    @Test
    public void testGetMissingItemReturnsNull() throws Exception {
        // Given an empty tree
        Tree tree = new Tree(id);

        // When you search for a missing item
        TreeItem item = tree.getItem("foo.txt");

        // Then it should return null
        assertNull(item);
    }

    @Test
    public void testGetItemByName() throws Exception {
        // Given a tree with an item
        String name = "foo.txt";
        TreeItem item = new TreeItem(Entry.Type.BLOB, "foo", name, 0);
        Tree tree = new Tree(id, Collections.singletonList(item));

        // When you search for that item
        TreeItem result = tree.getItem(name);

        // Then it's returned
        assertEquals(item, result);
    }

    @Test
    public void testAddItem() throws Exception {
        // Given an empty tree
        Tree tree = new Tree(id);

        // When you add an item to that tree
        TreeItem item = new TreeItem(Entry.Type.BLOB, "foo", "foo.txt", 0);
        tree.addItem(item);

        // Then tree items should be updated
        List<TreeItem> items = tree.getItems();
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    public void testRemoveItem() throws Exception {
        // Given a tree with 3 items
        TreeItem item1 = new TreeItem(Entry.Type.BLOB, "item1", "item1", 0);
        TreeItem item2 = new TreeItem(Entry.Type.BLOB, "item2", "item2", 0);
        TreeItem item3 = new TreeItem(Entry.Type.BLOB, "item3", "item3", 0);
        Tree tree = new Tree("tree");
        tree.addItem(item1);
        tree.addItem(item2);
        tree.addItem(item3);

        // When one of these items is removed
        tree.removeItem(item2.getId());

        // Then the other two items remain
        List<TreeItem> items = tree.getItems();
        assertEquals(2, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item3, items.get(1));
    }

}