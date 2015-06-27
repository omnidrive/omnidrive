package omnidrive.api.managers;

import omnidrive.api.auth.AuthTokens;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;
import omnidrive.api.base.DriveType;
import omnidrive.api.box.BoxAccount;
import omnidrive.api.dropbox.DropboxAccount;
import omnidrive.api.google.GoogleDriveAccount;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class AccountsManager extends Observable {

    private static AccountsManager manager = null;

    private final ApiManager apiManager = ApiManager.getApiManager();
    private final BaseAccount[] accounts = new BaseAccount[DriveType.length()];

    // singleton
    private AccountsManager() {

    }

    public static AccountsManager getAccountsManager() {
        if (manager == null) {
            manager = new AccountsManager();
        }

        return manager;
    }

    public void restoreAccounts(Map<DriveType, AuthTokens> accountsInfo) throws BaseException {
        for (DriveType type : accountsInfo.keySet()) {
            AuthTokens tokens = accountsInfo.get(type);
            BaseAccount account = createAccount(type, tokens);
            if (account != null) {
                setAccount(type, account);
            }
        }
    }

    public BaseAccount createAccount(DriveType type, AuthTokens tokens) throws BaseException {
        return this.apiManager.getApi(type).createAccount(tokens);
    }

    public void setAccount(DriveType type, BaseAccount account) {
        this.accounts[type.ordinal()] = account;

        notifyObservers(account);
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
