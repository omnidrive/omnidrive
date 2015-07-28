package omnidrive.api.dropbox;

import omnidrive.api.base.AccountType;
import omnidrive.api.base.BaseException;

public class DropboxException extends BaseException {

    public DropboxException(String message) {
        super(AccountType.Dropbox, message);
    }

}
