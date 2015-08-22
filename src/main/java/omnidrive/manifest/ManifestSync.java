package omnidrive.manifest;

import omnidrive.api.account.Account;

import java.util.List;

public interface ManifestSync {

    void uploadToAll(List<Account> accounts) throws Exception;

    void uploadToAccount(Account account) throws Exception;

    Manifest downloadFromAccount(Account account) throws Exception;

}
