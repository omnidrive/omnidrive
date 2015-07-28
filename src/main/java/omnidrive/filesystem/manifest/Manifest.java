package omnidrive.filesystem.manifest;

import omnidrive.api.auth.AuthToken;
import omnidrive.api.base.AccountType;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;

import java.util.Map;

public interface Manifest {

    Map<AccountType, AuthToken> getAuthTokens();

    void put(AccountType accountType, AuthToken authToken);

    void put(Entry entry);

    void remove(Entry entry);

    <T extends Entry> T get(String id, Class<T> clazz);

    Tree getRoot();

    long getUpdatedTime();

}
