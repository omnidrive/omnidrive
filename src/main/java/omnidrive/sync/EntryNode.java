package omnidrive.sync;

import omnidrive.algo.TreeNode;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.entry.Entry;
import omnidrive.manifest.entry.Tree;
import omnidrive.manifest.entry.TreeItem;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EntryNode implements TreeNode<EntryNode> {

    public static final Path MANIFEST_ROOT_PATH = Paths.get("");

    final private Manifest manifest;

    final private Path path;

    final private Entry entry;

    public EntryNode(Manifest manifest, Path path, Entry entry) {
        this.manifest = manifest;
        this.path = path;
        this.entry = entry;
    }

    public Path getPath() {
        return path;
    }

    public Entry.Type getType() {
        return entry.getType();
    }

    public <T extends Entry> T as(Class<T> clazz) {
        return clazz.cast(entry);
    }

    @Override
    public Map<String, EntryNode> getChildren() {
        Map<String, EntryNode> children = new HashMap<>();
        if (getType() == Entry.Type.TREE) {
            Tree tree = as(Tree.class);
            for (TreeItem item : tree.getItems()) {
                String id = item.getId();
                String name = item.getName();
                Path childPath = path.resolve(name);
                Entry childEntry = manifest.get(id);
                EntryNode node = new EntryNode(manifest, childPath, childEntry);
                children.put(name, node);
            }
        }
        return children;
    }

    public static EntryNode getRoot(Manifest manifest) {
        return new EntryNode(manifest, MANIFEST_ROOT_PATH, manifest.getRoot());
    }

}
