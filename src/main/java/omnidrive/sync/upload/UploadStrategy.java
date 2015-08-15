package omnidrive.sync.upload;

import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountException;
import omnidrive.exceptions.NoAccountFoundException;

import java.io.File;

public interface UploadStrategy {

    CloudAccount selectAccount(File file) throws AccountException, NoAccountFoundException;

}
