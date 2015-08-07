package omnidrive.api.auth;

import omnidrive.api.base.CloudAuthorizer;
import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountType;

public interface AuthService {

    void attempt(AccountType type, CloudAuthorizer authorizer, String authUrl);

    void report(AccountType type, String message);

    void succeed(AccountType type, CloudAccount account);

}
