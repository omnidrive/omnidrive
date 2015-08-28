package omnidrive.api.microsoft;

import omnidrive.api.account.AccountException;
import omnidrive.api.account.AccountType;

public class OneDriveException extends AccountException {
    public OneDriveException(String message, Exception original) {
        super(AccountType.OneDrive, message, original);
    }
}
