package omnidrive.api.box;

import javafx.scene.web.WebEngine;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseApi;

import com.box.sdk.BoxAPIConnection;
import omnidrive.api.base.BaseException;
import omnidrive.api.base.AccountType;

public class BoxApi extends BaseApi {

    // BOX App Keys
    private static final String APP_NAME = "Box";
    private static final String CLIENT_ID = "z4p9d2zjhmh15f4rsdzc4dbtm79e85xu";
    private static final String CLIENT_SECRET = "BwYmmj711tW47ETycwAMzyxL6xjXhteI";
    private static final String REDIRECT_URI = "https://app.box.com/services/poc_connector";

    private final BoxAPIConnection connection = new BoxAPIConnection(CLIENT_ID, CLIENT_SECRET);


    public BoxApi() {
        super(APP_NAME, CLIENT_ID, CLIENT_SECRET);
    }

    @Override
    public BaseAccount createAccount(String accessToken) throws BaseException {
        final BoxAPIConnection conn = new BoxAPIConnection(accessToken);
        return new BoxAccount(conn);
    }

    @Override
    public final String authorize() {
        String baseUrl = "https://www.box.com/api/oauth2/authorize?";
        String clientId = "client_id=" + CLIENT_ID;
        String responseType = "&response_type=code";
        String redirectUri = "&redirect_uri=" + REDIRECT_URI;

        return baseUrl + clientId + responseType + redirectUri;
    }

    @Override
    public final void fetchAuthCode(WebEngine engine) throws BaseException {
        String url = engine.getLocation();
        if (url.contains(REDIRECT_URI) && url.contains("code=")) {
            int codeStringIdx = url.indexOf("code=");
            String code = url.substring(codeStringIdx + "code=".length());
            finishAuthProcess(code);
        }
    }

    @Override
    public final void finishAuthProcess(String code) throws BaseException {
        this.connection.authenticate(code);
        this.connection.setAutoRefresh(true);
        BoxAccount boxAccount = new BoxAccount(this.connection);
        boxAccount.initialize();
        notifyAll(AccountType.Box, boxAccount);
    }

}
