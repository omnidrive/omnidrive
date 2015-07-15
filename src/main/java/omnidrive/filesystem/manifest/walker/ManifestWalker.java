package omnidrive.filesystem.manifest.walker;

import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;

public class ManifestWalker {

    final private Manifest manifest;

    public ManifestWalker(Manifest manifest) {
        this.manifest = manifest;
    }

    public void walk(ItemVisitor visitor) throws Exception {
        Tree root = manifest.getRoot();
        for (TreeItem item : root.getItems()) {
            walk(item, visitor);
        }
    }

    public void walk(TreeItem item, ItemVisitor visitor) throws Exception {
        switch (item.getType()) {
            case BLOB: visitor.visit(item); break;
            case TREE:
                visitor.preVisit(item);
                Tree tree = manifest.get(item.getId(), Tree.class);
                for (TreeItem child : tree.getItems()) {
                    walk(child, visitor);
                }
                visitor.postVisit(item);
                break;
        }
    }

}
