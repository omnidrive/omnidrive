package omnidrive.filesystem.sync.upload;

import omnidrive.api.base.Account;
import omnidrive.api.base.AccountException;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.exception.NoAccountFoundException;

import java.io.File;

/**
 * This strategy selects an account to upload the file to by walking over
 * active accounts and finding one that has capacity for this file
 */
public class SimpleUploadStrategy implements UploadStrategy {

    final private AccountsManager accountsManager;

    public SimpleUploadStrategy(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public Account selectAccount(File file) throws AccountException, NoAccountFoundException {
        Account account = null;
        for (Account candidate : accountsManager.getActiveAccounts()) {
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
