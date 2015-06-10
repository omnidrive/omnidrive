package omnidrive.filesystem.manifest;

import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.entry.Tree;

public interface Storage {

    void put(Tree tree);

    void put(Blob blob);

    Tree getTree(String id);

    Blob getBlob(String id);

    void commit();

}
