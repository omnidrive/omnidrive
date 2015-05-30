package omnidrive.filesystem.sync;

import omnidrive.api.base.BaseAccount;

public interface UploadStrategy {

    BaseAccount selectAccount();

}
