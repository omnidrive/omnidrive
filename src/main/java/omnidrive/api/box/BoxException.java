package omnidrive.api.box;

import omnidrive.api.account.AccountType;
import omnidrive.api.account.AccountException;

public class BoxException extends AccountException {
    public BoxException(String message, Exception original) {
        super(AccountType.Box, message, original);
    }
}
