package omnidrive.filesystem.manifest.walker;

import omnidrive.filesystem.manifest.entry.TreeItem;

/**
 * Default implementation which does nothing - override only necessary methods
 */
public class SimpleVisitor implements ItemVisitor {

    @Override
    public void preVisit(TreeItem item) throws Exception {
        // no-op
    }

    @Override
    public void visit(TreeItem item) throws Exception {
        // no-op
    }

    @Override
    public void postVisit(TreeItem item) throws Exception {
        // no-op
    }

}
