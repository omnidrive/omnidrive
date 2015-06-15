package omnidrive.filesystem.manifest;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

public class MapDbManifest implements Manifest {

    public static final String ROOT_KEY = "root";

    public static final String TREES_MAP = "manifest-trees";

    public static final String BLOBS_MAP = "manifest-blobs";

    public static final String UPLOAD_MANIFEST_FILENAME = "manifest";

    final private File manifestFile;

    final private DB db;

    final private HTreeMap<String, Tree> trees;

    final private HTreeMap<String, Blob> blobs;

    public MapDbManifest(File manifestFile) {
        this.manifestFile = manifestFile;
        db = makeDb(manifestFile);
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

    public void sync(Collection<BaseAccount> accounts) throws Exception {
        db.commit();
        db.compact();
        for (BaseAccount account : accounts) {
            account.uploadFile(UPLOAD_MANIFEST_FILENAME,
                    new FileInputStream(manifestFile),
                    manifestFile.length());
        }
    }

    public void close() {
        db.commit();
        db.close();
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
