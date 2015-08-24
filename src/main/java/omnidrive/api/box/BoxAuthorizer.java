package omnidrive.api.box;

import javafx.scene.web.WebEngine;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.auth.AuthSecretKey;
import omnidrive.api.account.*;

import com.box.sdk.BoxAPIConnection;
import org.apache.http.auth.AUTH;

public class BoxAuthorizer extends AccountAuthorizer {

    // BOX App Keys
    private static final String APP_NAME = "Box";
    private static final String CLIENT_ID = "z4p9d2zjhmh15f4rsdzc4dbtm79e85xu";
    private static final String REDIRECT_URI = "https://app.box.com/services/poc_connector";
    private static final String AUTH_URL = "https://www.box.com/api/oauth2/authorize?";

    private final BoxAPIConnection connection;

    public BoxAuthorizer(AuthSecretFile secretFile) {
        super(APP_NAME, CLIENT_ID, secretFile, AuthSecretKey.Box);
        connection = new BoxAPIConnection(getAppId(), getAppSecret());
    }

    @Override
    public Account restoreAccount(AccountMetadata metadata) throws AccountException {
        BoxAPIConnection conn = new BoxAPIConnection(
                getAppId(),
                getAppSecret(),
                metadata.getAccessToken(),
                metadata.getRefreshToken()
        );

        return new BoxAccount(metadata, conn);
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
            this.connection.authenticate(code);
            this.connection.setAutoRefresh(true);

            AccountMetadata metadata = new AccountMetadata(
                    getAppId(),
                    getAppSecret(),
                    this.connection.getAccessToken(),
                    this.connection.getRefreshToken()
            );

            boxAccount = new BoxAccount(metadata, this.connection);
        } catch (Exception ex) {
            throw new BoxException("Failed to finish auth process.");
        }

        return boxAccount;
    }
}
