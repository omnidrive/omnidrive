package omnidrive.api.auth;

import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.AccountType;

public interface AuthService {

    void attempt(AccountType type, BaseApi api, String authUrl);

    void report(AccountType type, String message);

    void succeed(AccountType type, BaseAccount account);

}
