package omnidrive.api.google;

import omnidrive.api.base.AccountType;
import omnidrive.api.base.AccountException;

public class GoogleDriveException extends AccountException {

    public GoogleDriveException(String message) {
        super(AccountType.GoogleDrive, message);
    }

}
