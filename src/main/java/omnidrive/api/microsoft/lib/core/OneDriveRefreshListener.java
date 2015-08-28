package omnidrive.api.microsoft.lib.core;

import omnidrive.api.microsoft.lib.auth.OneDriveOAuth;

/**
 * Created by assafey on 8/28/15.
 */
public interface OneDriveRefreshListener {
    void onRefresh(OneDriveCore core, OneDriveOAuth newOAuth);
}
