package omnidrive.api.microsoft.lib.rest;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;


public class OneDriveRestApi extends RestApi {

    public static final String ONEDRIVE_API_AUTH_URL = "https://login.live.com/oauth20_authorize.srf";
    public static final String ONEDRIVE_API_REDIRECT_URL = "https://login.live.com/oauth20_desktop.srf";
    public static final String ONEDRIVE_API_AUTH_TOKEN_URL = "https://login.live.com/oauth20_token.srf";
    public static final String ONEDRIVE_API_LOGOUT_URL = "https://login.live.com/oauth20_logout.srf";

    public static final String ONEDRIVE_API_VERSION = "/v1.0";
    public static final String ONEDRIVE_API_URL = "https://api.onedrive.com" + ONEDRIVE_API_VERSION;

    public static final String ONEDRIVE_API_DRIVE = "/drive";
    public static final String ONEDRIVE_API_ITEM_BY_PATH = "/root:";
    public static final String ONEDRIVE_API_ITEM_BY_ID = "/items";
    public static final String ONEDRIVE_API_ROOT = "/root";
    public static final String ONEDRIVE_API_CONTENT = "/content";
    public static final String ONEDRIVE_API_CHILDREN = "/children";

    public static final String ONEDRIVE_API_EXPAND_CHILDREN_QUERY = "?expand=children";

    private String accessToken;

    public OneDriveRestApi(String accessToken) {

        this.accessToken = accessToken;
    }

    public JSONObject doGet(String host, String path, String query) throws Exception {
        return super.doGet(host, path, addAccessTokenToApiQuery(host, query));
    }

    public JSONObject doPost(String host, String path, String query, HashMap<String, String> bodyParams) throws Exception {
        return super.doPost(host, path, addAccessTokenToApiQuery(host, query), bodyParams);
    }

    public JSONObject doPost(String host, String path, String query, JSONObject jsonBody) throws Exception {
        return super.doPost(host, path, addAccessTokenToApiQuery(host, query), jsonBody);
    }

    public JSONObject doPost(String host, String path, String query, byte[] body) throws Exception {
        return super.doPost(host, path, addAccessTokenToApiQuery(host, query), body);
    }

    public JSONObject doPost(String host, String path, String query, InputStream bodyStream) throws Exception {
        return super.doPost(host, path, addAccessTokenToApiQuery(host, query), bodyStream);
    }

    public JSONObject doPost(String host, String path, String query, String bodyString) throws Exception {
        return super.doPost(host, path, addAccessTokenToApiQuery(host, query), bodyString);
    }

    public JSONObject doPut(String host, String path, String query, HashMap<String, String> bodyParams) throws Exception {
        return super.doPut(host, path, addAccessTokenToApiQuery(host, query), bodyParams);
    }

    public JSONObject doPut(String host, String path, String query, JSONObject jsonBody) throws Exception {
        return super.doPut(host, path, addAccessTokenToApiQuery(host, query), jsonBody);
    }

    public JSONObject doPut(String host, String path, String query, byte[] body) throws Exception {
        return super.doPut(host, path, addAccessTokenToApiQuery(host, query), body);
    }

    public JSONObject doPut(String host, String path, String query, InputStream bodyStream) throws Exception {
        return super.doPut(host, path, addAccessTokenToApiQuery(host, query), bodyStream);
    }

    public JSONObject doPut(String host, String path, String query, String bodyString) throws Exception {
        return super.doPut(host, path, addAccessTokenToApiQuery(host, query), bodyString);
    }

    public JSONObject doDelete(String host, String path, String query) throws Exception {
        return super.doDelete(host, path, addAccessTokenToApiQuery(host, query));
    }

    public void changeAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    private String addAccessTokenToApiQuery(String host, String query) {
        if (accessToken == null || !host.equals(ONEDRIVE_API_URL)) {
            return query;
        } else {
            if (query != null && !query.isEmpty()) {
                return query += "&access_token=" + accessToken;
            } else {
                return "?access_token=" + accessToken;
            }
        }
    }
}
