package omnidrive.api.microsoft.lib.rest;

import com.ning.http.client.*;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class RestApi {

    public static final String REST_API_CONTENT_TYPE_JSON = "application/json";
    public static final String REST_API_CONTENT_TYPE_TEXT = "text/plain";
    public static final String REST_API_CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String REST_API_CONTENT_TYPE_BINARY = "application/octet-stream";

    private final AsyncHttpClient client = new AsyncHttpClient();

    public JSONObject doGet(String host, String path, String query) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_FORM);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String query, HashMap<String, String> bodyParams) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_FORM);
        builder.setBody(toBodyString(bodyParams));
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String query, JSONObject jsonBody) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_JSON);
        builder.setBody(jsonBody.toString());
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String query, byte[] body) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_BINARY);
        builder.setBody(body);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String query, InputStream bodyStream) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_BINARY);
        builder.setBody(bodyStream);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String query, String bodyString) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_TEXT);
        builder.setBody(bodyString);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String query, HashMap<String, String> bodyParams) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_FORM);
        builder.setBody(toBodyString(bodyParams));
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String query, JSONObject jsonBody) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_JSON);
        builder.setBody(jsonBody.toString());
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String query, byte[] body) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_BINARY);
        builder.setBody(body);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String query, InputStream bodyStream) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_BINARY);
        builder.setBody(bodyStream);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String query, String bodyString) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_TEXT);
        builder.setBody(bodyString);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doDelete(String host, String path, String query) throws Exception {
        String url = toUrl(host, path, query);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareDelete(url);
        builder.addHeader("Content-Type", REST_API_CONTENT_TYPE_FORM);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    private String toUrl(String host, String path, String query) {
        if (host == null) {
            return null;
        }

        if (!host.endsWith("/")) {
            host += "/";
        }

        String url = host;

        if (path != null && !path.isEmpty()) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            url += path;
        }

        // fix path+host url
        url = url.replace("://", "###");
        url = url.replace("//", "/");
        url = url.replace("###", "://");

        if (query != null && !query.isEmpty()) {
            if (!query.startsWith("?")) {
                query = "?" + query;
            }

            url += query;
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
