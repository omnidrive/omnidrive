package omnidrive.filesystem.manifest;

import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;

public interface Manifest {

    void put(Tree tree);

    void put(Blob blob);

    Tree getRoot();

    Tree getTree(String id);

    Blob getBlob(String id);

}
