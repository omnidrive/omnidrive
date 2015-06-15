package omnidrive.filesystem.manifest;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;

import java.util.Collection;

public interface Manifest {

    void put(Entry entry);

    Tree getRoot();

    Tree getTree(String id);

    Blob getBlob(String id);

    void sync(Collection<BaseAccount> accounts) throws Exception;

    void close();

}
