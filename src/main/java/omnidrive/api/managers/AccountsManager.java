package omnidrive.api.managers;

import omnidrive.api.account.*;
import omnidrive.api.account.event.AccountEvent;
import omnidrive.api.account.event.AccountRefreshedEvent;
import omnidrive.api.account.event.AccountRemovedEvent;
import omnidrive.api.account.event.NewAccountAddedEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class AccountsManager extends Observable implements RefreshedAccountObserver {

    private final AuthManager authManager = AuthManager.getAuthManager();

    private final Account[] accounts = new Account[AccountType.length()];

    public void restoreAccounts(Map<String, AccountMetadata> accountsInfo) throws AccountException {
        for (Map.Entry<String, AccountMetadata> entry : accountsInfo.entrySet()) {
            AccountType type = AccountType.valueOf(entry.getKey().replace(" ", ""));
            AccountMetadata metadata = entry.getValue();
            Account account = restoreAccount(type, metadata);
            if (account != null) {
                account.initialize();
                setAccount(account);
            }
        }
    }

    public Account restoreAccount(AccountType type, AccountMetadata metadata) throws AccountException {
        return this.authManager.getAuthorizer(type).restoreAccount(metadata, this /*also notify when account refreshed*/);
    }

    public void setAccount(Account account) {
        this.accounts[account.getType().ordinal()] = account;
    }

    @Override
    public void onAccountRefreshed(Account accountToRefresh) {
        fireEvent(new AccountRefreshedEvent(accountToRefresh));
    }

    public void addNewAccount(Account accountToAdd) {
        setAccount(accountToAdd);
        accountToAdd.addRefreshedAccountObserver(this);
        fireEvent(new NewAccountAddedEvent(accountToAdd));
    }

    public void removeAccount(AccountType type) {
        if (this.accounts[type.ordinal()] != null) {
            Account accountToRemove = this.accounts[type.ordinal()];
            fireEvent(new AccountRemovedEvent(accountToRemove));
            this.accounts[type.ordinal()] = null;
        }
    }

    private void fireEvent(AccountEvent event) {
        setChanged();
        notifyObservers(event);
        clearChanged();
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
