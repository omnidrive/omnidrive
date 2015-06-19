package omnidrive.filesystem.manifest;

import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

public class MapDbManifest implements Manifest {

    public static final String ROOT_KEY = "root";

    public static final String TREES_MAP = "manifest-trees";

    public static final String BLOBS_MAP = "manifest-blobs";

    final private HTreeMap<String, Tree> trees;

    final private HTreeMap<String, Blob> blobs;

    public MapDbManifest(DB db) {
        trees = db.getHashMap(TREES_MAP);
        blobs = db.getHashMap(BLOBS_MAP);
        initRoot();
    }

    public Tree getRoot() {
        return getTree(ROOT_KEY);
    }

    public void put(Entry entry) {
        switch (entry.getType()) {
            case TREE:
                trees.put(entry.getId(), (Tree) entry);
                break;
            case BLOB:
                blobs.put(entry.getId(), (Blob) entry);
                break;
        }
    }

    public Tree getTree(String id) {
        return trees.get(id);
    }

    public Blob getBlob(String id) {
        return blobs.get(id);
    }

    private void initRoot() {
        if (getRoot() == null) {
            put(new Tree(ROOT_KEY));
        }
    }

}
