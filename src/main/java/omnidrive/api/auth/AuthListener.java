package omnidrive.api.auth;

import omnidrive.api.account.Account;
import omnidrive.api.account.AccountType;

public interface AuthListener {

    void authSucceed(Account account);

    void authFailure(AccountType type, String error);

}
