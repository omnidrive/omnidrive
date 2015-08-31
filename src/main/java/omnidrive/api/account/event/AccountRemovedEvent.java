package omnidrive.api.account.event;

import omnidrive.api.account.Account;

public class AccountRemovedEvent extends AccountEvent {

    public AccountRemovedEvent(Account account) {
        super(account);
    }
    
}
