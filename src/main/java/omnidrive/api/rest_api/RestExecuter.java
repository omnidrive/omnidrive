package omnidrive.api.rest_api;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import org.apache.http.HttpStatus;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class RestExecuter {

    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();


    public RestExecuter() {

    }

    public void executeGet(String url, List<Param> params, final PropertyChangeListener listener) {
        String urlGet = prepareUrlGetRequest(url, params);

        AsyncHttpClient.BoundRequestBuilder builder = this.asyncHttpClient.prepareGet(urlGet);
        //builder.addHeader("Content-type", "application/x-www-form-urlencoded");

        builder.execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                if (response.getStatusCode() == HttpStatus.SC_OK) {
                    String body = response.getResponseBody();
                    listener.propertyChange(new PropertyChangeEvent(this, null, null, body));
                } else {
                    listener.propertyChange(new PropertyChangeEvent(this, null, null, null));
                }
                return response;
            }
        });
    }

    public void executePost(String url, List<Param> params, final PropertyChangeListener listener) {
        AsyncHttpClient.BoundRequestBuilder builder = this.asyncHttpClient.preparePost(url);
        builder.addHeader("Content-type", "application/x-www-form-urlencoded");
        builder.setFormParams(params);

        builder.execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                if (response.getStatusCode() == HttpStatus.SC_OK) {
                    String body = response.getResponseBody();
                    listener.propertyChange(new PropertyChangeEvent(this, null, null, body));
                } else {
                    listener.propertyChange(new PropertyChangeEvent(this, null, null, null));
                }
                return response;
            }
        });
    }

    private String prepareUrlGetRequest(String url, List<Param> params) {
        String urlGet = url;
        boolean firstParam = true;

        if (params != null) {
            for (Param param : params) {
                RestParam restParam = new RestParam(param, firstParam);
                urlGet += restParam.toString();
                if (firstParam) {
                    firstParam = false;
                }
            }
        }

        return urlGet;
    }

}
