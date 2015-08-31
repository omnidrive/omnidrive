package omnidrive.api.account.event;

import omnidrive.api.account.Account;

public class AccountRefreshedEvent extends AccountEvent {

    public AccountRefreshedEvent(Account account) {
        super(account);
    }
    
}
