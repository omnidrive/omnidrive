package omnidrive.api.auth;

import omnidrive.api.base.AccountAuthorizer;
import omnidrive.api.base.Account;
import omnidrive.api.base.AccountType;

public interface AuthService {

    void attemptToAuth(AccountType type, AccountAuthorizer authorizer, String authUrl);

    void reportAuthError(AccountType type, String message);

    void accountAuthorized(AccountType type, Account account);

}
