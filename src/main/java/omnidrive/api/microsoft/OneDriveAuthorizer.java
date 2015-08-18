package omnidrive.api.microsoft;

import javafx.scene.web.WebEngine;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.auth.AuthSecretKey;
import omnidrive.api.account.*;
import omnidrive.api.microsoft.lib.core.OneDriveCore;
import omnidrive.api.microsoft.lib.auth.OneDriveOAuth;
import omnidrive.api.microsoft.lib.rest.OneDriveRestApi;
import omnidrive.api.microsoft.lib.auth.OneDriveScope;

public class OneDriveAuthorizer extends AccountAuthorizer {

    private static final String APP_NAME = "omnidrive";
    private static final String APP_ID = "000000004C14C243";

    private static final String APP_SCOPE = OneDriveScope.toQuery(
            OneDriveScope.SignIn,
            OneDriveScope.OfflineAccess,
            OneDriveScope.ReadWrite
    );

    public OneDriveAuthorizer(AuthSecretFile secretFile) {
        super(APP_NAME, APP_ID, secretFile, AuthSecretKey.OneDrive);
    }

    @Override
    public Account recreateAccount(String accessToken, String refreshToken) throws AccountException {
        OneDriveOAuth oauth = new OneDriveOAuth(getAppId(), getAppSecret(), accessToken, refreshToken);
        OneDriveCore core = new OneDriveCore(oauth);
        try {
            core.refreshAuthorization();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to refresh token");
        }

        AccountMetadata metadata = new AccountMetadata(getAppId(), getAppSecret(), accessToken, refreshToken);

        OneDriveAccount account = new OneDriveAccount(metadata, core);

        account.initialize();

        return account;
    }

    @Override
    public String authUrl() {
        return OneDriveRestApi.ONEDRIVE_API_AUTH_URL +
                "?client_id=" + getAppId() +
                "&scope=" + APP_SCOPE +
                "&response_type=code" +
                "&redirect_uri=" + OneDriveRestApi.ONEDRIVE_API_REDIRECT_URL;
    }

    @Override
    public void fetchAuthCode(WebEngine engine) throws AccountException {
        String url = engine.getLocation();
        if (url.contains(OneDriveRestApi.ONEDRIVE_API_REDIRECT_URL)) {
            if (url.contains("?code=")) {
                int indexOfCodeString = url.indexOf("?code=");
                String code = url.substring(indexOfCodeString + "?code=".length());
                code = code.substring(0, code.indexOf("&"));
                finishAuthProcess(code);
            }
        }
    }

    @Override
    public void finishAuthProcess(String code) throws AccountException {
        try {
            OneDriveCore core = OneDriveCore.authorize(getAppId(), getAppSecret(), code);

            AccountMetadata metadata = new AccountMetadata(
                    getAppId(),
                    getAppSecret(),
                    core.getOauth().getAccessToken(),
                    core.getOauth().getRefreshToken()
            );

            OneDriveAccount account = new OneDriveAccount(metadata, core);

            account.initialize();
            notifyAll(AccountType.OneDrive, account);
        } catch (Exception ex) {
            throw new OneDriveException("Failed to authorize");
        }
    }
}
