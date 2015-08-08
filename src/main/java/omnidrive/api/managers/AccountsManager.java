package omnidrive.api.managers;

import omnidrive.api.base.AccountMetadata;
import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class AccountsManager extends Observable {

    private final ApiManager apiManager = ApiManager.getApiManager();

    private final CloudAccount[] accounts = new CloudAccount[AccountType.length()];

    public void restoreAccounts(Map<String, String> accountsInfo) throws AccountException {
        for (Map.Entry<String, String> entry : accountsInfo.entrySet()) {
            AccountType type = AccountType.valueOf(entry.getKey());
            String accessToken = entry.getValue();
            CloudAccount account = createAccount(type, accessToken);
            if (account != null) {
                setAccount(type, account);
            }
        }
    }

    public CloudAccount createAccount(AccountType type, String accessToken) throws AccountException {
        return this.apiManager.getApi(type).createAccount(accessToken);
    }

    public void setAccount(AccountType type, CloudAccount account) {
        this.accounts[type.ordinal()] = account;
        setChanged();
        notifyObservers(account);
    }

    public void removeAccount(AccountType type) {
        if (this.accounts[type.ordinal()] != null) {
            this.accounts[type.ordinal()] = null;
        }
    }

    public CloudAccount getAccount(AccountType type) {
        return this.accounts[type.ordinal()];
    }

    public List<CloudAccount> getActiveAccounts() {
        List<CloudAccount> activeAccounts = new LinkedList<>();

        for (CloudAccount account : this.accounts) {
            if (account != null) {
                activeAccounts.add(account);
            }
        }

        return activeAccounts;
    }

    public long getCloudFreeSize() throws AccountException {
        long size = 0;

        List<CloudAccount> accounts = getActiveAccounts();
        for (CloudAccount account : accounts) {
            size += account.getCachedQuotaRemainingSize();
        }

        return size;
    }

    public long getCloudTotalSize() throws AccountException {
        long size = 0;

        List<CloudAccount> accounts = getActiveAccounts();
        for (CloudAccount account : accounts) {
            size += account.getCachedQuotaTotalSize();
        }

        return size;
    }

    public boolean isRegistered(AccountType type) {
        return this.accounts[type.ordinal()] != null;
    }

    public AccountType toType(CloudAccount account) {
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
