package omnidrive.api.microsoft;

import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountType;

public class OneDriveException extends AccountException {
    public OneDriveException(String message) {
        super(AccountType.OneDrive, message);
    }
}
