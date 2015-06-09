package omnidrive.filesystem.entry;

import omnidrive.filesystem.BaseTest;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.*;

public class TreeTest extends BaseTest {

    @Test
    public void testCreateTreeFromFile() throws Exception {
        File file = getResource("foo");
        Tree tree = new Tree(file);

        //noinspection ResultOfMethodCallIgnored
        UUID.fromString(tree.getId());
        TreeItem[] items = tree.getItems();
        assertEquals(1, items.length);
    }
}