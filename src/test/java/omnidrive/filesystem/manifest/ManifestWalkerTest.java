package omnidrive.filesystem.manifest;

import omnidrive.api.base.DriveType;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.filesystem.manifest.walker.ManifestWalker;
import omnidrive.filesystem.manifest.walker.Visitor;
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
    public void testWalkManifestFromRoot() throws Exception {
        // Given some files and dir exist in manifest
        Blob bar = new Blob("bar", 10, DriveType.Dropbox);
        Tree foo = new Tree("foo", Collections.singletonList(
                new TreeItem(Entry.Type.BLOB, "bar", "bar.txt", 0)));
        Blob baz = new Blob("baz", 20, DriveType.Dropbox);
        manifest.put(foo);
        manifest.put(bar);
        manifest.put(baz);
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        root.addItem(new TreeItem(Entry.Type.BLOB, "baz", "baz.txt", 0));
        manifest.put(root);

        // When walking the manifest using a visitor
        walker.walk(manifest.getRoot(), visitor);

        // Then all IDs under tree should be registered
        String expected =
                "pre visit 'root'\n" +
                "pre visit 'foo'\n" +
                "visit 'bar'\n" +
                "post visit 'foo'\n" +
                "visit 'baz'\n" +
                "post visit 'root'\n";
        assertEquals(expected, visitor.getResult());
    }

    @Test
    public void testWalkManifestSubtree() throws Exception {
        // Given some files and dir exist in manifest
        Blob bar = new Blob("bar", 10, DriveType.Dropbox);
        Blob baz = new Blob("baz", 20, DriveType.Dropbox);
        Blob qux = new Blob("qux", 30, DriveType.Dropbox);
        Tree foo = new Tree("foo", Arrays.asList(
                new TreeItem(Entry.Type.BLOB, "bar", "bar.txt", 0),
                new TreeItem(Entry.Type.BLOB, "baz", "baz.txt", 0)));
        manifest.put(foo);
        manifest.put(bar);
        manifest.put(baz);
        manifest.put(qux);
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        root.addItem(new TreeItem(Entry.Type.BLOB, "qux", "qux.txt", 0));
        manifest.put(root);

        // When walking the manifest using a visitor
        walker.walk(foo, visitor);

        // Then all IDs under tree should be registered
        String expected =
                "pre visit 'foo'\n" +
                "visit 'bar'\n" +
                "visit 'baz'\n" +
                "post visit 'foo'\n";
        assertEquals(expected, visitor.getResult());
    }

    @Test
    public void testWalkBlob() throws Exception {
        // Given some files and dir exist in manifest
        Blob bar = new Blob("bar", 10, DriveType.Dropbox);
        Tree foo = new Tree("foo", Collections.singletonList(
                new TreeItem(Entry.Type.BLOB, "bar", "bar.txt", 0)));
        Blob baz = new Blob("baz", 20, DriveType.Dropbox);
        manifest.put(foo);
        manifest.put(bar);
        manifest.put(baz);
        Tree root = manifest.getRoot();
        root.addItem(new TreeItem(Entry.Type.TREE, "foo", "foo", 0));
        root.addItem(new TreeItem(Entry.Type.BLOB, "baz", "baz.txt", 0));
        manifest.put(root);

        // When walking the manifest using a visitor
        walker.walk(bar, visitor);

        // Then all IDs under tree should be registered
        String expected = "visit 'bar'\n";
        assertEquals(expected, visitor.getResult());
    }

    private Manifest createMemoryManifest() {
        DB db = MapDbUtils.createMemoryDb();
        return new MapDbManifest(db);
    }

    private class LoggingVisitor implements Visitor {

        final private StringBuilder stringBuilder = new StringBuilder();

        @Override
        public void preVisitTree(Tree tree) throws Exception {
            append("pre ", tree);
        }

        @Override
        public void visitBlob(Blob blob) throws Exception {
            append("", blob);
        }

        @Override
        public void postVisitTree(Tree tree) throws Exception {
            append("post ", tree);
        }

        public String getResult() {
            return stringBuilder.toString();
        }

        private void append(String prefix, Entry entry) {
            stringBuilder
                .append(prefix)
                .append("visit '")
                .append(entry.getId())
                .append("'\n");
        }

    }
}