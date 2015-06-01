package omnidrive.filesystem.sync;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;

public class SimpleUploadStrategy implements UploadStrategy {

    final private AccountsManager accountsManager;

    @Inject
    public SimpleUploadStrategy(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public BaseAccount selectAccount() {
        return null;
    }

}
