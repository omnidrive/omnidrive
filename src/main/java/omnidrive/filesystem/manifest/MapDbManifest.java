package omnidrive.filesystem.manifest;

import omnidrive.api.base.AccountMetadata;
import omnidrive.api.base.AccountType;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
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

    final private HTreeMap<AccountType, AccountMetadata> accountsMetadata;

    final private HTreeMap<String, Entry> entries;

    final private Atomic.Long updateTime;

    public MapDbManifest(DB db) {
        accountsMetadata = db.getHashMap(AUTH_TOKENS_MAP);
        entries = db.getHashMap(ENTRIES_MAP);
        updateTime = db.getAtomicLong(UPDATE_TIME);
        initRoot();
    }

    public Tree getRoot() {
        return get(ROOT_KEY, Tree.class);
    }

    public Map<AccountType, AccountMetadata> getAccountsMetadata() {
        return new HashMap<>(accountsMetadata);
    }

    public void put(AccountType accountType, AccountMetadata metadata) {
        accountsMetadata.put(accountType, metadata);
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
