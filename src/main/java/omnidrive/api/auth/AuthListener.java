package omnidrive.api.auth;

import omnidrive.api.base.Account;
import omnidrive.api.base.AccountType;

public interface AuthListener {

    void authSucceed(AccountType type, Account account);

    void authFailure(AccountType type, String error);

}
