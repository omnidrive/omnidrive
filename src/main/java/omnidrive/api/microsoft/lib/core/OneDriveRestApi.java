package omnidrive.api.microsoft.lib.core;

import com.ning.http.client.*;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class OneDriveRestApi {

    private AsyncHttpClient client = new AsyncHttpClient();

    public JSONObject doGet(String host, String path, String accessToken) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareGet(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, HashMap<String, String> bodyParams) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBody(toBodyString(bodyParams));
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, JSONObject jsonBody) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBody(jsonBody.toString());
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, byte[] body) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBody(body);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPost(String host, String path, String accessToken, InputStream bodyStream) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePost(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBody(bodyStream);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, HashMap<String, String> bodyParams) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBody(toBodyString(bodyParams));
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, JSONObject jsonBody) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBody(jsonBody.toString());
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, byte[] body) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBody(body);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doPut(String host, String path, String accessToken, InputStream bodyStream) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.preparePut(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setBody(bodyStream);
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    public JSONObject doDelete(String host, String path, String accessToken) throws Exception {
        String url = toUrl(host, path, accessToken);
        AsyncHttpClient.BoundRequestBuilder builder = client.prepareDelete(url);
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        JSONObject response = builder.execute(new OneDriveAsyncHandler()).get();
        return response;
    }

    private String toUrl(String host, String path, String accessToken) {
        String url = host;

        if (path != null && !path.isEmpty()) {
            url += "/" + path;
        }

        if (accessToken != null) {
            return url += "?access_token=" + accessToken;
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
            if (httpResponseStatus.getStatusCode() >= 300) {
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
            String jsonString = bytes.toString();
            return new JSONObject(jsonString);
        }
    }
}
