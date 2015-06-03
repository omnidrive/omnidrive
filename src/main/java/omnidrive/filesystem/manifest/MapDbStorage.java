package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import omnidrive.filesystem.entry.TreeMetadata;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.io.Serializable;

public class MapDbStorage implements Storage {

    public static final String MAP_NAME = "manifest";

    final private DB db;

    final private HTreeMap<String, Serializable> map;

    @Inject
    public MapDbStorage(DB db) {
        this.db = db;
        map = db.getHashMap(MAP_NAME);
    }

    public MapDbStorage(File file) {
        this(makeDb(file));
    }

    public void put(String id, Serializable metadata) {
        map.put(id, metadata);
    }

    public TreeMetadata get(String id) {
        return (TreeMetadata) map.get(id);
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
