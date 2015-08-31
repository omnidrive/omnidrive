package omnidrive.manifest.mapdb;

import omnidrive.api.account.AccountMetadata;
import omnidrive.api.account.AccountType;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.entry.Entry;
import omnidrive.manifest.entry.Tree;
import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.HashMap;
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

}
