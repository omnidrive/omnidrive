package omnidrive.filesystem.sync;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.exception.NoAccountFoundException;

import java.io.File;

public class SimpleUploadStrategy implements UploadStrategy {

    final private AccountsManager accountsManager;

    @Inject
    public SimpleUploadStrategy(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public BaseAccount selectAccount(File file) throws BaseException, NoAccountFoundException {
        BaseAccount account = null;
        for (BaseAccount candidate : accountsManager.getActiveAccounts()) {
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
