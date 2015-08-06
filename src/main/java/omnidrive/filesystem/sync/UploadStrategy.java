package omnidrive.filesystem.sync;

import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountException;
import omnidrive.filesystem.exception.NoAccountFoundException;

import java.io.File;

public interface UploadStrategy {

    CloudAccount selectAccount(File file) throws AccountException, NoAccountFoundException;

}
