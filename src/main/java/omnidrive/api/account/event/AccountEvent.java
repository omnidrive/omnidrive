package omnidrive.api.account.event;

import omnidrive.api.account.Account;

public abstract class AccountEvent {

    final private Account account;

    public AccountEvent(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

}
