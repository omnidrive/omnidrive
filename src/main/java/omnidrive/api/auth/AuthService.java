package omnidrive.api.auth;

import omnidrive.api.base.CloudApi;
import omnidrive.api.base.Account;
import omnidrive.api.base.AccountType;

public interface AuthService {

    void attempt(AccountType type, CloudApi api, String authUrl);

    void report(AccountType type, String message);

    void succeed(AccountType type, Account account);

}
