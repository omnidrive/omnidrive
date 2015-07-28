package omnidrive.api.google;

import omnidrive.api.base.AccountType;
import omnidrive.api.base.BaseException;

public class GoogleDriveException extends BaseException {

    public GoogleDriveException(String message) {
        super(AccountType.GoogleDrive, message);
    }

}
