package omnidrive.api.google;

import omnidrive.api.account.AccountType;
import omnidrive.api.account.AccountException;

public class GoogleDriveException extends AccountException {

    public GoogleDriveException(String message) {
        super(AccountType.GoogleDrive, message);
    }

}
