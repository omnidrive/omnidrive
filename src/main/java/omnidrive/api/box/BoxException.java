package omnidrive.api.box;

import omnidrive.api.base.AccountType;
import omnidrive.api.base.AccountException;

public class BoxException extends AccountException {
    public BoxException(String message) {
        super(AccountType.Box, message);
    }
}
