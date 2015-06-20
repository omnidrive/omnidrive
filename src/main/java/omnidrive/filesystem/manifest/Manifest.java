package omnidrive.filesystem.manifest;

import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;

public interface Manifest {

    void put(Entry entry);

    void remove(Entry entry);

    <T extends Entry> T get(String id, Class<T> clazz);

    Tree getRoot();

}
