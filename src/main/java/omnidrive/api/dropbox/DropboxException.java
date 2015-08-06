package omnidrive.api.dropbox;

import omnidrive.api.base.AccountType;
import omnidrive.api.base.AccountException;

public class DropboxException extends AccountException {

    public DropboxException(String message) {
        super(AccountType.Dropbox, message);
    }

}
