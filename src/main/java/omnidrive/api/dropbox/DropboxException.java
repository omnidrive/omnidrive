package omnidrive.api.dropbox;

import omnidrive.api.account.AccountType;
import omnidrive.api.account.AccountException;

public class DropboxException extends AccountException {

    public DropboxException(String message, Exception original) {
        super(AccountType.Dropbox, message, original);
    }

}
