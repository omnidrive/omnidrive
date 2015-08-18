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

    private final BoxAPIConnection connection;

    public BoxAuthorizer(AuthSecretFile secretFile) {
        super(APP_NAME, CLIENT_ID, secretFile, AuthSecretKey.Box);
        connection = new BoxAPIConnection(getAppId(), getAppSecret());
    }

    @Override
    public Account recreateAccount(String accessToken, String refreshToken) throws AccountException {
        BoxAPIConnection conn = new BoxAPIConnection(
                getAppId(),
                getAppSecret(),
                accessToken,
                refreshToken
        );

        AccountMetadata metadata = new AccountMetadata(getAppId(), getAppSecret(), accessToken, refreshToken);

        return new BoxAccount(metadata, conn);
    }

    @Override
    public final String authUrl() {
        String baseUrl = "https://www.box.com/api/oauth2/authorize?";
        String clientId = "client_id=" + getAppId();
        String responseType = "&response_type=code";
        String redirectUri = "&redirect_uri=" + REDIRECT_URI;

        return baseUrl + clientId + responseType + redirectUri;
    }

    @Override
    public final void fetchAuthCode(WebEngine engine) throws AccountException {
        String url = engine.getLocation();
        if (url.contains(REDIRECT_URI) && url.contains("code=")) {
            int codeStringIdx = url.indexOf("code=");
            String code = url.substring(codeStringIdx + "code=".length());
            finishAuthProcess(code);
        }
    }

    @Override
    public final void finishAuthProcess(String code) throws AccountException {
        this.connection.authenticate(code);
        this.connection.setAutoRefresh(true);

        AccountMetadata metadata = new AccountMetadata(
                getAppId(),
                getAppSecret(),
                this.connection.getAccessToken(),
                this.connection.getRefreshToken()
        );

        BoxAccount boxAccount = new BoxAccount(metadata, this.connection);

        boxAccount.initialize();
        notifyAll(AccountType.Box, boxAccount);
    }

}
