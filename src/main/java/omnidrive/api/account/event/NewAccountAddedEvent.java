package omnidrive.api.account.event;

import omnidrive.api.account.Account;

public class NewAccountAddedEvent extends AccountEvent {

    public NewAccountAddedEvent(Account account) {
        super(account);
    }

}
