package omnidrive.filesystem.manifest;

import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;

public interface Manifest {

    void put(Entry entry);

    Tree getRoot();

    Tree getTree(String id);

    Blob getBlob(String id);

}
