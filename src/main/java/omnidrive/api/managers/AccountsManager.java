package omnidrive.api.managers;

import omnidrive.api.base.Account;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountMetadata;
import omnidrive.api.base.AccountType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class AccountsManager extends Observable {

    private final AuthManager authManager = AuthManager.getAuthManager();

    private final Account[] accounts = new Account[AccountType.length()];

    public void restoreAccounts(Map<String, AccountMetadata> accountsInfo) throws AccountException {
        for (Map.Entry<String, AccountMetadata> entry : accountsInfo.entrySet()) {
            AccountType type = AccountType.valueOf(entry.getKey());
            AccountMetadata metadata = entry.getValue();
            Account account = restoreAccount(type, metadata.getAccessToken(), metadata.getRefreshToken());
            if (account != null) {
                setAccount(type, account);
            }
        }
    }

    public Account restoreAccount(AccountType type, String accessToken, String refreshToken) throws AccountException {
        return this.authManager.getAuthorizer(type).recreateAccount(accessToken, refreshToken);
    }

    public void setAccount(AccountType type, Account account) {
        this.accounts[type.ordinal()] = account;
        setChanged();
        notifyObservers(account);
    }

    public void removeAccount(AccountType type) {
        if (this.accounts[type.ordinal()] != null) {
            this.accounts[type.ordinal()] = null;
        }
    }

    public Account getAccount(AccountType type) {
        return this.accounts[type.ordinal()];
    }

    public List<Account> getActiveAccounts() {
        List<Account> activeAccounts = new LinkedList<>();

        for (Account account : this.accounts) {
            if (account != null) {
                activeAccounts.add(account);
            }
        }

        return activeAccounts;
    }

    public boolean hasActiveAccounts() {
        return getActiveAccounts().size() > 0;
    }

    public long getCloudFreeSize() throws AccountException {
        long size = 0;

        List<Account> accounts = getActiveAccounts();
        for (Account account : accounts) {
            size += account.getCachedQuotaRemainingSize();
        }

        return size;
    }

    public long getCloudTotalSize() throws AccountException {
        long size = 0;

        List<Account> accounts = getActiveAccounts();
        for (Account account : accounts) {
            size += account.getCachedQuotaTotalSize();
        }

        return size;
    }

    public boolean isRegistered(AccountType type) {
        return this.accounts[type.ordinal()] != null;
    }

    public AccountType toType(Account account) {
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
