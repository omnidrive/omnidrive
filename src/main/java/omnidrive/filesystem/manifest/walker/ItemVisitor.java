package omnidrive.filesystem.manifest.walker;

import omnidrive.filesystem.manifest.entry.TreeItem;

public interface ItemVisitor {

    void preVisit(TreeItem item) throws Exception;

    void visit(TreeItem item) throws Exception;

    void postVisit(TreeItem item) throws Exception;

}
