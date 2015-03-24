package omnidrive.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

public class TreeTest {

    private TreeEntry entry1;

    private TreeEntry entry2;

    private TreeEntry entry3;

    private Tree tree;

    @Before
    public void setUp() {
        entry1 = new TreeEntry(Object.Type.TREE, Hash.of("subtree"), "subtree");
        entry2 = new TreeEntry(Object.Type.BLOB, Hash.of("file1"), "file1.txt");
        entry3 = new TreeEntry(Object.Type.BLOB, Hash.of("file2"), "file2.txt");
        tree = new Tree(Arrays.asList(entry1, entry2, entry3));
    }

    @After
    public void tearDown() {
        entry1 = null;
        entry2 = null;
        entry3 = null;
        tree = null;
    }

    @Test
    public void testGetType() {
        assertEquals(Object.Type.TREE, tree.getType());
    }

    @Test
    public void testGetHash() {
        assertEquals(new Hash("52881429486ff28a1af0153fef93594f48989e00"), tree.getHash());
    }

    @Test
    public void testIteration() {
        Iterator<TreeEntry> iterator = tree.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(entry1, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(entry2, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(entry3, iterator.next());

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testWrite() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        tree.write(out);

        String contents =
                "tree 165\0" +
                "tree\tab421ff7a9a1031af9801e9e08dcc47593bb4028\tsubtree\n" +
                "blob\t60b27f004e454aca81b0480209cce5081ec52390\tfile1.txt\n" +
                "blob\tcb99b709a1978bd205ab9dfd4c5aaa1fc91c7523\tfile2.txt";

        assertEquals(contents, out.toString());
    }

}