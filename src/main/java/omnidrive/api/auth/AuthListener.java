package omnidrive.api.auth;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.AccountType;

public interface AuthListener {

    void authenticated(AccountType type, BaseAccount account);

    void failure(AccountType type, String error);

}
