package omnidrive.filesystem.manifest.storage;

import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;

public interface Storage {

    void put(Tree tree);

    void put(Blob blob);

    Tree getTree(String id);

    Blob getBlob(String id);

    void commit();

}
