package omnidrive.filesystem.manifest;

import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

public class MapDbManifest implements Manifest {

    public static final String ROOT_KEY = "root";

    public static final String ENTRIES_MAP = "manifest-entries";

    final private HTreeMap<String, Entry> entries;

    public MapDbManifest(DB db) {
        entries = db.getHashMap(ENTRIES_MAP);
        initRoot();
    }

    public Tree getRoot() {
        return get(ROOT_KEY, Tree.class);
    }

    public void put(Entry entry) {
        entries.put(entry.getId(), entry);
    }

    public void remove(Entry entry) {
        entries.remove(entry.getId());
    }

    public <T extends Entry> T get(String id, Class<T> clazz) {
        return clazz.cast(entries.get(id));
    }

    private void initRoot() {
        if (getRoot() == null) {
            put(new Tree(ROOT_KEY));
        }
    }

}
