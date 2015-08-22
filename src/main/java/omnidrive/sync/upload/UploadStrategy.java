package omnidrive.sync.upload;

import omnidrive.api.account.Account;
import omnidrive.api.account.AccountException;
import omnidrive.exceptions.NoAccountFoundException;

import java.io.File;

public interface UploadStrategy {

    Account selectAccount(File file) throws AccountException, NoAccountFoundException;

}
