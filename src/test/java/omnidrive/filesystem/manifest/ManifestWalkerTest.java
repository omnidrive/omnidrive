package omnidrive.filesystem.manifest;

import omnidrive.api.base.AccountType;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.filesystem.manifest.walker.ItemVisitor;
import omnidrive.filesystem.manifest.walker.ManifestWalker;
import omnidrive.util.MapDbUtils;
import org.junit.Test;
import org.mapdb.DB;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ManifestWalkerTest {

    private Manifest manifest = createMemoryManifest();

    private ManifestWalker walker = new ManifestWalker(manifest);

    private LoggingVisitor visitor = new LoggingVisitor();

    @Test
    public void testWalkManifestSubtree() throws Exception {
        // Given some files and dir exist in manifest
        Blob bar = new Blob("bar", 10, AccountType.Dropbox);
        Blob baz = new Blob("baz", 20, AccountType.Dropbox);
        Blob qux = new Blob("qux", 30, AccountType.Dropbox);
        Tree foo = new Tree("foo", Arrays.asList(
                new TreeItem(Entry.Type.BLOB, "bar", "bar.txt", 0),
                new TreeItem(Entry.Type.BLOB, "baz", "baz.txt", 0)));
        manifest.put(foo);
        manifest.put(bar);
        manifest.put(baz);
        manifest.put(qux);
        Tree root = manifest.getRoot();
        TreeItem fooTreeItem = new TreeItem(Entry.Type.TREE, "foo", "foo", 0);
        root.addItem(fooTreeItem);
        root.addItem(new TreeItem(Entry.Type.BLOB, "qux", "qux.txt", 0));
        manifest.put(root);

        // When walking the manifest using a visitor
        walker.walk(fooTreeItem, visitor);

        // Then all IDs under tree should be registered
        String expected =
                "pre visit 'foo'\n" +
                "visit 'bar.txt'\n" +
                "visit 'baz.txt'\n" +
                "post visit 'foo'\n";
        assertEquals(expected, visitor.getResult());
    }

    @Test
    public void testWalkManifestRoot() throws Exception {
        // Given some files and dir exist in manifest
        Blob bar = new Blob("bar", 10, AccountType.Dropbox);
        Blob baz = new Blob("baz", 20, AccountType.Dropbox);
        Tree foo = new Tree("foo", Collections.singletonList(new TreeItem(Entry.Type.BLOB, "bar", "bar.txt", 0)));
        manifest.put(foo);
        manifest.put(bar);
        manifest.put(baz);
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        root.addItem(new TreeItem(Entry.Type.BLOB, "baz", "baz.txt", 0));
        manifest.put(root);

        // When walking the manifest using a visitor
        walker.walk(visitor);

        // Then all IDs under tree should be registered
        String expected =
                "pre visit 'foo'\n" +
                "visit 'bar.txt'\n" +
                "post visit 'foo'\n" +
                "visit 'baz.txt'\n";
        assertEquals(expected, visitor.getResult());
    }

    @Test
    public void testWalkBlob() throws Exception {
        // Given some files and dir exist in manifest
        Blob bar = new Blob("bar", 10, AccountType.Dropbox);
        TreeItem barTreeItem = new TreeItem(Entry.Type.BLOB, "bar", "bar.txt", 0);
        Tree foo = new Tree("foo", Collections.singletonList(barTreeItem));
        Blob baz = new Blob("baz", 20, AccountType.Dropbox);
        manifest.put(foo);
        manifest.put(bar);
        manifest.put(baz);
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        root.addItem(new TreeItem(Entry.Type.BLOB, "baz", "baz.txt", 0));
        manifest.put(root);

        // When walking the manifest using a visitor
        walker.walk(barTreeItem, visitor);

        // Then all IDs under tree should be registered
        String expected = "visit 'bar.txt'\n";
        assertEquals(expected, visitor.getResult());
    }

    private Manifest createMemoryManifest() {
        DB db = MapDbUtils.createMemoryDb();
        return new MapDbManifest(db);
    }

    private class LoggingVisitor implements ItemVisitor {

        final private StringBuilder stringBuilder = new StringBuilder();

        @Override
        public void preVisit(TreeItem item) throws Exception {
            append("pre ", item);
        }

        @Override
        public void visit(TreeItem item) throws Exception {
            append("", item);
        }

        @Override
        public void postVisit(TreeItem item) throws Exception {
            append("post ", item);
        }

        public String getResult() {
            return stringBuilder.toString();
        }

        private void append(String prefix, TreeItem item) {
            stringBuilder
                .append(prefix)
                .append("visit '")
                .append(item.getName())
                .append("'\n");
        }
    }
}