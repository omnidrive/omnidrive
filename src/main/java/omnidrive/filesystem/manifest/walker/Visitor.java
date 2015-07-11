package omnidrive.filesystem.manifest.walker;

import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;

public interface Visitor {

    void preVisitTree(Tree tree) throws Exception;

    void visitBlob(Blob blob) throws Exception;

    void postVisitTree(Tree tree) throws Exception;

}
