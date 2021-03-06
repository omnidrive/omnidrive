package omnidrive.manifest;

import omnidrive.api.account.AccountMetadata;
import omnidrive.api.account.AccountType;
import omnidrive.manifest.entry.Entry;
import omnidrive.manifest.entry.Tree;

import java.util.Map;

public interface Manifest {

    Map<String, AccountMetadata> getAccountsMetadata();

    void put(AccountType accountType, AccountMetadata metadata);

    void remove(AccountType accountType);

    void put(Entry entry);

    void remove(Entry entry);

    Entry get(String id);

    <T extends Entry> T get(String id, Class<T> clazz);

    Tree getRoot();

    long getUpdatedTime();

}
