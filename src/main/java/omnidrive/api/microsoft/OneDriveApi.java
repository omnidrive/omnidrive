package omnidrive.api.microsoft;

import javafx.scene.web.WebEngine;
import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseException;

import com.ning.http.client.*;

import com.google.gson.*;
import omnidrive.api.managers.LoginManager;

import java.util.ArrayList;
import java.util.List;

public class OneDriveApi extends BaseApi {

    private static final String APP_NAME = "OneDrive";
    private static final String CLIENT_ID = "000000004C14C243";
    private static final String CLIENT_SECRET = "4Xucj-d2MSpbnxXJ8dbkhK3Bi1XWFUTC";
    private static final String SCOPE = "wl.signin onedrive.readwrite wl.offline_access";

    private static final String AuthUrl = "https://login.live.com/oauth20_authorize.srf";
    private static final String TokenUrl = "https://login.live.com/oauth20_token.srf";
    private static final String RedirectUrl = "http://en.wikipedia.org/wiki/static_web_page";

    public OneDriveApi() {
        super(APP_NAME, CLIENT_ID, CLIENT_SECRET);
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public final String authorize() {
        String url = AuthUrl;

        url += "?client_id=" + CLIENT_ID;
        url += "&scope=" + SCOPE;
        url += "&response_type=code";
        url += "&redirect_uri=" + RedirectUrl;

        return url;
    }

    public final void fetchAuthCode(WebEngine engine) throws BaseException {
        String url = engine.getLocation();
        if (url.startsWith(RedirectUrl) && url.contains("?code=")) {
            String code = url.substring(url.indexOf("?code=") + "?code=".length()).trim();
            finishAuthProcess(code);
        }
    }

    public final void finishAuthProcess(String code) throws BaseException {
        List<Param> params = new ArrayList<Param>();
        params.add(new Param("client_id", CLIENT_ID));
        params.add(new Param("client_secret", CLIENT_SECRET));
        params.add(new Param("grant_type", "authorization_code"));
        params.add(new Param("code", code));
        params.add(new Param("redirect_uri", RedirectUrl));

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder builder = asyncHttpClient.preparePost(TokenUrl);
        builder.addHeader("Content-type", "application/x-www-form-urlencoded");
        builder.setFormParams(params);
        builder.execute(new AsyncCompletionHandler<Response>() {

            @Override
            public Response onCompleted(Response response) throws Exception {
                String body = response.getResponseBody();
                Gson gson = new Gson();
                OneDriveAuthProperties properties = gson.fromJson(body, OneDriveAuthProperties.class);

                String accessToken = properties.getAccessToken();
                String refreshToken = properties.getRefreshToken();
                if (accessToken != null && refreshToken != null) {
                    notifyLoginListeners(new OneDriveUser(properties));
                } else {
                    OneDriveAuthError error = gson.fromJson(body, OneDriveAuthError.class);
                    LoginManager.getLoginManager().showError(error.getErrorDescription());
                }

                return response;
            }
        });
    }
}
