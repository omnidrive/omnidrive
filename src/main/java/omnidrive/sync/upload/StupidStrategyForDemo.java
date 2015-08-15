package omnidrive.sync.upload;

import omnidrive.api.base.AccountException;
import omnidrive.api.base.CloudAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.exception.NoAccountFoundException;

import java.io.File;
import java.util.List;

/**
 * This strategy selects an account to upload the file to by
 * randomly selecting from active accounts list
 */
public class StupidStrategyForDemo implements UploadStrategy {

    final private AccountsManager accountsManager;

    private int current = 0;

    public StupidStrategyForDemo(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public CloudAccount selectAccount(File file) throws AccountException, NoAccountFoundException {
        List<CloudAccount> accounts = accountsManager.getActiveAccounts();
        CloudAccount account = accounts.get(current % accounts.size());
        current++;
        return account;
    }

}
