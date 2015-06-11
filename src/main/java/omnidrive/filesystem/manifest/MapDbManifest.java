package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;

public class MapDbManifest implements Manifest {

    public static final String ROOT_KEY = "root";

    public static final String TREES_MAP = "manifest-trees";

    public static final String BLOBS_MAP = "manifest-blobs";

    final private HTreeMap<String, Tree> trees;

    final private HTreeMap<String, Blob> blobs;

    @Inject
    public MapDbManifest(DB db) {
        trees = db.getHashMap(TREES_MAP);
        blobs = db.getHashMap(BLOBS_MAP);
        initRoot();
    }

    public MapDbManifest(File file) {
        this(makeDb(file));
    }

    public Tree getRoot() {
        return getTree(ROOT_KEY);
    }

    public void put(Tree tree) {
        trees.put(tree.getId(), tree);
    }

    public void put(Blob blob) {
        blobs.put(blob.getId(), blob);
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

    private static DB makeDb(File file) {
        return DBMaker.newFileDB(file)
                .closeOnJvmShutdown()
                .make();
    }

}
