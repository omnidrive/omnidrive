package omnidrive.filesystem.sync;

import omnidrive.api.base.Account;
import omnidrive.api.base.AccountException;
import omnidrive.filesystem.exception.NoAccountFoundException;

import java.io.File;

public interface UploadStrategy {

    Account selectAccount(File file) throws AccountException, NoAccountFoundException;

}
