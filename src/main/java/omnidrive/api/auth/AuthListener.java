package omnidrive.api.auth;

import omnidrive.api.base.Account;
import omnidrive.api.base.AccountType;

public interface AuthListener {

    void authenticated(AccountType type, Account account);

    void failure(AccountType type, String error);

}
