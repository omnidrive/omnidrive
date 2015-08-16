package omnidrive.api.microsoft.lib.core;

import com.ning.http.client.*;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class OneDriveRestApi {

    public static final String ONEDRIVE_API_AUTH_URL = "https://login.live.com/oauth20_authorize.srf";
    public static final String ONEDRIVE_API_REDIRECT_URL = "https://login.live.com/oauth20_desktop.srf";
    public static final String ONEDRIVE_API_AUTH_TOKEN_URL = "https://login.live.com/oauth20_token.srf";

    public static final String ONEDRIVE_API_VERSION = "/v1.0";
    public static final String ONEDRIVE_API_URL = "https://api.onedrive.com" + ONEDRIVE_API_VERSION;

    public static final String ONEDRIVE_API_DRIVE = "/drive";
    public static final String ONEDRIVE_API_ITEM_BY_PATH = "/root:";
    public static final String ONEDRIVE_API_ITEM_BY_ID = "/items";
    public static final String ONEDRIVE_API_ROOT = "/root";
    public static final String ONEDRIVE_API_CONTENT = "/content";
    public static final String ONEDRIVE_API_CHILDREN = "/children";

    public static final String ONEDRIVE_API_EXPAND_CHILDREN = "?expand=children";

    public static final String REST_API_CONTENT_TYPE_JSON = "application/json";
    public static final String REST_API_CONTENT_TYPE_TEXT = "text/plain";
    public static final String REST_API_CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String REST_API_CONTENT_TYPE_BINARY = "application/octet-stream";

    private AsyncHttpClient client = new AsyncHttpClient();

    public JSONObject doGet(String host, String path, String accessToken) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_FORM);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, HashMap<String, String> bodyParams) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_FORM);
        builder.setBody(toBodyString(bodyParams));
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, JSONObject jsonBody) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_JSON);
        builder.setBody(jsonBody.toString());
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, byte[] body) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_BINARY);
        builder.setBody(body);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, InputStream bodyStream) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_BINARY);
        builder.setBody(bodyStream);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, String bodyString) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_TEXT);
        builder.setBody(bodyString);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, HashMap<String, String> bodyParams) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_FORM);
        builder.setBody(toBodyString(bodyParams));
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, JSONObject jsonBody) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_JSON);
        builder.setBody(jsonBody.toString());
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, byte[] body) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_BINARY);
        builder.setBody(body);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, InputStream bodyStream) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_BINARY);
        builder.setBody(bodyStream);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, String bodyString) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_TEXT);
        builder.setBody(bodyString);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doDelete(String host, String path, String accessToken) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareDelete(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_FORM);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    private String toUrl(String host, String path, String accessToken) {
        String url = host;

        if (path != null && !path.isEmpty()) {
            url += path;
        }

        url = url.replace("://", "###");
        url = url.replace("//", "/");
        url = url.replace("###", "://");

        if (accessToken != null) {
            if (url.contains("?")) {
                url += "&access_token=" + accessToken;
            } else {
                url += "?access_token=" + accessToken;
            }
        }

        return url;
    }

    private String toBodyString(HashMap<String, String> bodyParams) {
        String body = "";
        for (String key : bodyParams.keySet()) {
            String value = bodyParams.get(key);
            if (body.isEmpty()) {
                body += key + "=" + value;
            } else {
                body += "&" + key + "=" + value;
            }
        }
        return body;
    }

    private class OneDriveAsyncHandler implements AsyncHandler<JSONObject> {
        private ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        @Override
        public void onThrowable(Throwable throwable) {

        }

        @Override
        public STATE onBodyPartReceived(HttpResponseBodyPart httpResponseBodyPart) throws Exception {
            bytes.write(httpResponseBodyPart.getBodyPartBytes());
            return STATE.CONTINUE;
        }

        @Override
        public STATE onStatusReceived(HttpResponseStatus httpResponseStatus) throws Exception {
            int statusCode = httpResponseStatus.getStatusCode();
            if (statusCode >= 300) {
                return STATE.ABORT;
            }

            return STATE.CONTINUE;
        }

        @Override
        public STATE onHeadersReceived(HttpResponseHeaders httpResponseHeaders) throws Exception {
            return STATE.CONTINUE;
        }

        @Override
        public JSONObject onCompleted() throws Exception {
            if (bytes.size() > 0) {
                String jsonString = bytes.toString();
                return new JSONObject(jsonString);
            } else {
                return null;
            }
        }
    }
}
