package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

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

    @Override
    public void put(String id, Serializable metadata) {
        map.put(id, metadata);
    }

    @Override
    public void commit() {
        db.commit();
    }

}
