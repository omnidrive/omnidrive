package omnidrive.filesystem.sync;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;
import omnidrive.filesystem.exception.NoAccountFoundException;

import java.io.File;

public interface UploadStrategy {

    BaseAccount selectAccount(File file) throws BaseException, NoAccountFoundException;

}
