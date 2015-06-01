package omnidrive.api.managers;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.DriveType;
import omnidrive.api.box.BoxAccount;
import omnidrive.api.dropbox.DropboxAccount;
import omnidrive.api.google.GoogleDriveAccount;

import java.util.LinkedList;
import java.util.List;

public class AccountsManager {

    private static AccountsManager manager = null;

    private final BaseAccount[] accounts = new BaseAccount[DriveType.values().length];

    // singleton
    private AccountsManager() {

    }

    public static AccountsManager getAccountsManager() {
        if (manager == null) {
            manager = new AccountsManager();
        }

        return manager;
    }

    public void setAccount(DriveType type, BaseAccount account) {
        this.accounts[type.ordinal()] = account;
    }

    public void removeAccount(DriveType type) {
        if (this.accounts[type.ordinal()] != null) {
            this.accounts[type.ordinal()] = null;
        }
    }

    public BaseAccount getAccount(DriveType type) {
        return this.accounts[type.ordinal()];
    }

    public List<BaseAccount> getActiveAccounts() {
        List<BaseAccount> activeAccounts = new LinkedList<BaseAccount>();

        for (BaseAccount account : this.accounts) {
            if (account != null) {
                activeAccounts.add(account);
            }
        }

        return activeAccounts;
    }

    public boolean isRegistered(DriveType type) {
        return this.accounts[type.ordinal()] != null;
    }

    public DriveType toType(BaseAccount account) {
        DriveType type = null;

        if (account instanceof DropboxAccount) {
            type = DriveType.Dropbox;
        } else if (account instanceof GoogleDriveAccount) {
            type = DriveType.GoogleDrive;
        } else if (account instanceof BoxAccount) {
            type = DriveType.Box;
        }

        return type;
    }
}
