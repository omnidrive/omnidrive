package omnidrive.filesystem.manifest.walker;

import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;

public class ManifestWalker {

    final private Manifest manifest;

    public ManifestWalker(Manifest manifest) {
        this.manifest = manifest;
    }

    public void walk(TreeItem item, Visitor visitor) throws Exception {
        String id = item.getId();
        switch (item.getType()) {
            case BLOB: walk(manifest.get(id, Blob.class), visitor); break;
            case TREE: walk(manifest.get(id, Tree.class), visitor); break;
        }
    }

    public void walk(Tree tree, Visitor visitor) throws Exception {
        visitor.preVisitTree(tree);
        for (TreeItem item : tree.getItems()) {
            walk(item, visitor);
        }
        visitor.postVisitTree(tree);
    }

    public void walk(Blob blob, Visitor visitor) throws Exception {
        visitor.visitBlob(blob);
    }

}
