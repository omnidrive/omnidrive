package omnidrive.api.microsoft;

import javafx.scene.web.WebEngine;
import omnidrive.api.base.Account;
import omnidrive.api.base.AccountAuthorizer;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountType;
import omnidrive.api.microsoft.lib.core.OneDriveCore;
import omnidrive.api.microsoft.lib.auth.OneDriveOAuth;
import omnidrive.api.microsoft.lib.rest.OneDriveRestApi;
import omnidrive.api.microsoft.lib.auth.OneDriveScope;

public class OneDriveAuthorizer extends AccountAuthorizer {

    private static final String APP_NAME = "omnidrive";
    private static final String APP_ID = "000000004C14C243";
    private static final String APP_SECRET = "4Xucj-d2MSpbnxXJ8dbkhK3Bi1XWFUTC";

    private static final String APP_SCOPE = OneDriveScope.toQuery(
            OneDriveScope.SignIn,
            OneDriveScope.OfflineAccess,
            OneDriveScope.ReadWrite
    );

    public OneDriveAuthorizer() {
        super(APP_NAME, APP_ID, APP_SECRET);
    }

    @Override
    public Account recreateAccount(String accessToken, String refreshToken) throws AccountException {
        OneDriveOAuth oauth = new OneDriveOAuth(APP_ID, APP_SECRET, accessToken, refreshToken);
        OneDriveCore core = new OneDriveCore(oauth);
        try {
            core.refreshAuthorization();
        } catch (Exception ex) {
            throw new OneDriveException("Failed to refresh token");
        }
        OneDriveAccount account = new OneDriveAccount(core);
        account.initialize();
        return account;
    }

    @Override
    public String authUrl() {
        return OneDriveRestApi.ONEDRIVE_API_AUTH_URL +
                "?client_id=" + APP_ID +
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
            OneDriveCore core = OneDriveCore.authorize(APP_ID, APP_SECRET, code);
            OneDriveAccount account = new OneDriveAccount(core);
            account.initialize();
            notifyAll(AccountType.OneDrive, account);
        } catch (Exception ex) {
            throw new OneDriveException("Failed to authorize");
        }
    }
}
