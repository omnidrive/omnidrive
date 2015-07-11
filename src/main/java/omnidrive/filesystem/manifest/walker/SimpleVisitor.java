package omnidrive.filesystem.manifest.walker;

import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;

/**
 * Default implementation which does nothing - override only necessary methods
 */
public class SimpleVisitor implements Visitor {

    @Override
    public void preVisitTree(Tree tree) throws Exception {
        // no-op
    }

    @Override
    public void visitBlob(Blob blob) throws Exception {
        // no-op
    }

    @Override
    public void postVisitTree(Tree tree) throws Exception {
        // no-op
    }

}
