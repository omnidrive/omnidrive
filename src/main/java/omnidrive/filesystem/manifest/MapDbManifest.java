package omnidrive.filesystem.manifest;

import omnidrive.api.auth.AuthToken;
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

    final private HTreeMap<AccountType, AuthToken> authTokens;

    final private HTreeMap<String, Entry> entries;

    final private Atomic.Long updateTime;

    public MapDbManifest(DB db) {
        authTokens = db.getHashMap(AUTH_TOKENS_MAP);
        entries = db.getHashMap(ENTRIES_MAP);
        updateTime = db.getAtomicLong(UPDATE_TIME);
        initRoot();
    }

    public Tree getRoot() {
        return get(ROOT_KEY, Tree.class);
    }

    public Map<AccountType, AuthToken> getAuthTokens() {
        return new HashMap<AccountType, AuthToken>(authTokens);
    }

    public void put(AccountType accountType, AuthToken authToken) {
        authTokens.put(accountType, authToken);
    }

    public void put(Entry entry) {
        entries.put(entry.getId(), entry);
        setUpdateTime();
    }

    public void remove(Entry entry) {
        entries.remove(entry.getId());
        setUpdateTime();
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
