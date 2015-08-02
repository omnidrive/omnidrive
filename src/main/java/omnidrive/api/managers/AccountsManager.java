package omnidrive.api.managers;

import omnidrive.api.base.AccountMetadata;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;
import omnidrive.api.base.AccountType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class AccountsManager extends Observable {

    private final ApiManager apiManager = ApiManager.getApiManager();

    private final BaseAccount[] accounts = new BaseAccount[AccountType.length()];

    public void restoreAccounts(Map<AccountType, AccountMetadata> accountsInfo) throws BaseException {
        for (AccountType type : accountsInfo.keySet()) {
            AccountMetadata metadata = accountsInfo.get(type);
            BaseAccount account = createAccount(type, metadata.getAccessToken());
            if (account != null) {
                setAccount(type, account);
            }
        }
    }

    public BaseAccount createAccount(AccountType type, String accessToken) throws BaseException {
        return this.apiManager.getApi(type).createAccount(accessToken);
    }

    public void setAccount(AccountType type, BaseAccount account) {
        this.accounts[type.ordinal()] = account;
        notifyObservers(account);
    }

    public void removeAccount(AccountType type) {
        if (this.accounts[type.ordinal()] != null) {
            this.accounts[type.ordinal()] = null;
        }
    }

    public BaseAccount getAccount(AccountType type) {
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

    public long getFreeSpaceSize() throws BaseException {
        long size = 0;

        List<BaseAccount> accounts = getActiveAccounts();
        for (BaseAccount account : accounts) {
            size += account.getQuotaRemainingSize();
        }

        return size;
    }

    public long getUsedSpaceSize() throws BaseException {
        long size = 0;

        List<BaseAccount> accounts = getActiveAccounts();
        for (BaseAccount account : accounts) {
            size += account.getQuotaTotalSize();
        }

        return size;
    }

    public boolean isRegistered(AccountType type) {
        return this.accounts[type.ordinal()] != null;
    }

    public AccountType toType(BaseAccount account) {
        AccountType type = null;

        for (AccountType candidate : AccountType.values()) {
            if (accounts[candidate.ordinal()] == account) {
                type = candidate;
                break;
            }
        }

        return type;
    }

}
