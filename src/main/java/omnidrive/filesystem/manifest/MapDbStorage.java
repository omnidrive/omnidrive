package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import omnidrive.filesystem.entry.BlobMetadata;
import omnidrive.filesystem.entry.TreeMetadata;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;

public class MapDbStorage implements Storage {

    public static final String TREES_MAP = "manifest-trees";

    public static final String BLOBS_MAP = "manifest-blobs";

    final private DB db;

    final private HTreeMap<String, TreeMetadata> trees;

    final private HTreeMap<String, BlobMetadata> blobs;

    @Inject
    public MapDbStorage(DB db) {
        this.db = db;
        trees = db.getHashMap(TREES_MAP);
        blobs = db.getHashMap(BLOBS_MAP);
    }

    public MapDbStorage(File file) {
        this(makeDb(file));
    }

    public void put(String id, TreeMetadata metadata) {
        trees.put(id, metadata);
    }

    public void put(String id, BlobMetadata metadata) {
        blobs.put(id, metadata);
    }

    public TreeMetadata getTreeMetadata(String id) {
        return trees.get(id);
    }

    public BlobMetadata getBlobMetadata(String id) {
        return blobs.get(id);
    }

    public void commit() {
        db.commit();
    }

    private static DB makeDb(File file) {
        return DBMaker.newFileDB(file)
                .closeOnJvmShutdown()
                .make();
    }

}
