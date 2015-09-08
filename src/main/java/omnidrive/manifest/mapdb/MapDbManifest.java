package omnidrive.manifest.mapdb;

import omnidrive.api.account.AccountMetadata;
import omnidrive.api.account.AccountType;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.entry.Blob;
import omnidrive.manifest.entry.Entry;
import omnidrive.manifest.entry.Tree;
import omnidrive.manifest.entry.TreeItem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapDbManifest implements Manifest {

    public static final String ROOT_KEY = "root";

    public static final String AUTH_TOKENS_MAP = "auth-tokens";

    public static final String ENTRIES_MAP = "manifest-entries";

    public static final String UPDATE_TIME = "update-time";

    final private DB db;

    final private HTreeMap<String, AccountMetadata> accountsMetadata;

    final private HTreeMap<String, Entry> entries;

    final private Atomic.Long updateTime;

    public MapDbManifest(DB db) {
        this.db = db;
        accountsMetadata = db.getHashMap(AUTH_TOKENS_MAP);
        entries = db.getHashMap(ENTRIES_MAP);
        updateTime = db.getAtomicLong(UPDATE_TIME);
        initRoot();
    }

    public MapDbManifest(DB db, boolean debug) {
        this(db);
        if (debug) {
            ManifestDebugger debugger = new ManifestDebugger(this);
            Thread thread = new Thread(debugger);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public Tree getRoot() {
        return get(ROOT_KEY, Tree.class);
    }

    public Map<String, AccountMetadata> getAccountsMetadata() {
        return new HashMap<>(accountsMetadata);
    }

    public void put(AccountType accountType, AccountMetadata metadata) {
        accountsMetadata.put(accountType.toString(), metadata);
        setUpdateTime();
        db.commit();
    }

    public void remove(AccountType accountType) {
        accountsMetadata.remove(accountType.toString());
        setUpdateTime();
        db.commit();
    }

    public void put(Entry entry) {
        entries.put(entry.getId(), entry);
        setUpdateTime();
    }

    public void remove(Entry entry) {
        entries.remove(entry.getId());
        setUpdateTime();
    }

    public Entry get(String id) {
        return entries.get(id);
    }

    public <T extends Entry> T get(String id, Class<T> clazz) {
        return clazz.cast(entries.get(id));
    }

    public long getUpdatedTime() {
        return updateTime.get();
    }

    private void initRoot() {
        if (getRoot() == null) {
            put(new Tree(ROOT_KEY));
        }
    }

    private void setUpdateTime() {
        updateTime.set(System.currentTimeMillis());
    }

    private class ManifestDebugger implements Runnable {

        private final Manifest manifest;

        public ManifestDebugger(Manifest manifest) {
            this.manifest = manifest;
        }

        @Override
        public void run() {
            String manifestDebugPath = "/tmp/manideb.json";
            System.out.println("Debug manifest path: " + manifestDebugPath);

            while (true) {
                File file = new File(manifestDebugPath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception ex) {
                        System.out.println("Failed to create: " + manifestDebugPath);
                    }
                }

                try {
                    FileWriter writer = new FileWriter(manifestDebugPath);
                    writer.write(buildJson().toString());
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

                sleep();
            }
        }

        private JSONObject buildJson() throws Exception {
            JSONObject jsonObject = new JSONObject();
            walkFolder(new File("/").toPath(), manifest.getRoot().getItems(), jsonObject);
            return jsonObject;
        }

        private void walkFolder(Path parent, List<TreeItem> items, JSONObject jsonObject) throws Exception {
            for (int index = 0; index < items.size(); index++) {
                walkFile(parent, items, index, jsonObject);
            }
        }

        private void walkFile(Path parent, List<TreeItem> items, int index, JSONObject jsonObject) throws Exception {
            if (index < items.size()) {
                TreeItem item = items.get(index);
                if (item.getType() == Entry.Type.BLOB) {
                    File file = new File(parent.toString(), item.getName());
                    Blob blob = (Blob) manifest.get(item.getId());

                    // "/folder/file.txt" : ['Dropbox', 2048]
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(blob.getAccount().name());
                    jsonArray.put(blob.getSize());
                    jsonObject.put(file.toPath().toString(), jsonArray);
                } else { //item.getType() == Entry.Type.TREE
                    File folder = new File(parent.toString(), item.getName());
                    Tree tree = (Tree) manifest.get(item.getId());
                    walkFolder(folder.toPath(), tree.getItems(), jsonObject);
                }
            }
        }

        private void sleep() {
            try { Thread.sleep(2500); }
            catch (Exception ex) { }
        }
    }

}
