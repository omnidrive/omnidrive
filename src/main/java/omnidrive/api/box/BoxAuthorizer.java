package omnidrive.api.box;

import javafx.scene.web.WebEngine;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.auth.AuthSecretKey;
import omnidrive.api.account.*;

import com.box.sdk.BoxAPIConnection;

public class BoxAuthorizer extends AccountAuthorizer {

    // BOX App Keys
    private static final String APP_NAME = "Box";
    private static final String CLIENT_ID = "z4p9d2zjhmh15f4rsdzc4dbtm79e85xu";
    private static final String REDIRECT_URI = "https://app.box.com/services/poc_connector";
    private static final String AUTH_URL = "https://www.box.com/api/oauth2/authorize?";

    public BoxAuthorizer(AuthSecretFile secretFile) {
        super(APP_NAME, CLIENT_ID, secretFile, AuthSecretKey.Box);
    }

    @Override
    public Account restoreAccount(AccountMetadata metadata, RefreshedAccountObserver observer) throws AccountException {
        BoxAPIConnection conn = new BoxAPIConnection(
                getAppId(),
                getAppSecret(),
                metadata.getAccessToken(),
                metadata.getRefreshToken()
        );

        BoxAccount account = new BoxAccount(metadata, conn, observer);
        //account.refreshAuthorization(); // BOX API does auto refresh here

        return account;
    }

    @Override
    public final String authUrl() {
        String clientId = "client_id=" + getAppId();
        String responseType = "&response_type=code";
        String redirectUri = "&redirect_uri=" + REDIRECT_URI;

        return AUTH_URL + clientId + responseType + redirectUri;
    }

    @Override
    public final Account authenticate(WebEngine engine) throws AccountException {
        Account account = null;

        String url = engine.getLocation();
        if (url.contains(REDIRECT_URI) && url.contains("code=")) {
            int codeStringIdx = url.indexOf("code=");
            String code = url.substring(codeStringIdx + "code=".length());
            account = createAccountFromAuthCode(code);
        }

        return account;
    }

    @Override
    public final Account createAccountFromAuthCode(String code) throws AccountException {
        BoxAccount boxAccount = null;

        try {
            BoxAPIConnection conn = new BoxAPIConnection(getAppId(), getAppSecret());
            conn.authenticate(code);
            conn.setAutoRefresh(true);

            AccountMetadata metadata = new AccountMetadata(
                    getAppId(),
                    getAppSecret(),
                    conn.getAccessToken(),
                    conn.getRefreshToken()
            );

            boxAccount = new BoxAccount(metadata, conn);
        } catch (Exception ex) {
            throw new BoxException("Failed to finish auth process.", ex);
        }

        return boxAccount;
    }
}
