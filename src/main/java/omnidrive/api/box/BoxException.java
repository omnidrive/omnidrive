package omnidrive.api.box;

import omnidrive.api.base.AccountType;
import omnidrive.api.base.BaseException;

public class BoxException extends BaseException {
    public BoxException(String message) {
        super(AccountType.Box, message);
    }
}
