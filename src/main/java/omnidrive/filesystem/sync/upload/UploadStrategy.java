package omnidrive.filesystem.sync.upload;

import omnidrive.api.account.Account;
import omnidrive.api.account.AccountException;
import omnidrive.filesystem.exception.NoAccountFoundException;

import java.io.File;

public interface UploadStrategy {

    Account selectAccount(File file) throws AccountException, NoAccountFoundException;

}
