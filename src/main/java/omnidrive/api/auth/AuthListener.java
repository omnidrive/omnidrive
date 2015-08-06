package omnidrive.api.auth;

import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountType;

public interface AuthListener {

    void authenticated(AccountType type, CloudAccount account);

    void failure(AccountType type, String error);

}
