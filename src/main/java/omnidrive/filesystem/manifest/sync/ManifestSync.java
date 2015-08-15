package omnidrive.filesystem.manifest.sync;

import omnidrive.filesystem.manifest.Manifest;
import omnidrive.api.base.Account;

import java.util.List;

public interface ManifestSync {

    void uploadToAll(List<Account> accounts) throws Exception;

    void uploadToAccount(Account account) throws Exception;

    Manifest downloadFromAccount(Account account) throws Exception;

}
