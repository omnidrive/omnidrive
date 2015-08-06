package omnidrive.filesystem.sync;

import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountException;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.exception.NoAccountFoundException;

import java.io.File;

public class SimpleUploadStrategy implements UploadStrategy {

    final private AccountsManager accountsManager;

    public SimpleUploadStrategy(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public CloudAccount selectAccount(File file) throws AccountException, NoAccountFoundException {
        CloudAccount account = null;
        for (CloudAccount candidate : accountsManager.getActiveAccounts()) {
            if (candidate.getQuotaRemainingSize() > file.length()) {
                account = candidate;
                break;
            }
        }
        if (account == null) {
            throw new NoAccountFoundException();
        }
        return account;
    }

}
