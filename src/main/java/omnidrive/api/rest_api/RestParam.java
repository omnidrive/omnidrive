package omnidrive.api.rest_api;

import com.ning.http.client.Param;

public class RestParam extends Param {

    private boolean isFirstParam;

    public RestParam(Param param, boolean isFirstParam) {
        super(param.getName(), param.getValue());

        this.isFirstParam = isFirstParam;
    }

    public RestParam(String name, String value, boolean isFirstParam) {
        super(name, value);

        this.isFirstParam = isFirstParam;
    }

    @Override
    public String toString() {
        if (isFirstParam) {
            return "?" + getName() + "=" + getValue();
        } else {
            return "&" + getName() + "=" + getValue();
        }
    }
}
