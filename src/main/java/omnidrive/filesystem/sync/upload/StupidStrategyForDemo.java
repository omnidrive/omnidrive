package omnidrive.filesystem.sync.upload;

import omnidrive.api.account.AccountException;
import omnidrive.api.account.Account;
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

    public Account selectAccount(File file) throws AccountException, NoAccountFoundException {
        List<Account> accounts = accountsManager.getActiveAccounts();
        Account account = accounts.get(current % accounts.size());
        current++;
        return account;
    }

}
