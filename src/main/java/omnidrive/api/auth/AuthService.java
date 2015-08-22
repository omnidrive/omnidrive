package omnidrive.api.auth;

import omnidrive.api.account.AccountAuthorizer;
import omnidrive.api.account.Account;
import omnidrive.api.account.AccountType;

public interface AuthService {

    void attemptToAuth(AccountType type, AccountAuthorizer authorizer, String authUrl);

    void reportAuthError(AccountType type, String message);

    void accountAuthorized(AccountType type, Account account);

}
