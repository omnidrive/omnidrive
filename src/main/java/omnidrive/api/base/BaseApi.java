package omnidrive.api.base;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseApi implements Authorizer {

    protected final List<AuthListener> listeners = new LinkedList<AuthListener>();

    private final String appName;
    private final String appId;
    private final String appSecret;

    public BaseApi(String appName, String appId, String appSecret) {
        this.appName = appName;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public String login(AuthListener listener) throws BaseException {
        addListener(listener);
        return authorize();
    }

    public String getName() {
        return this.appName;
    }

    protected String getAppId() {
        return this.appId;
    }

    protected String getAppSecret() {
        return this.appSecret;
    }

    protected void notifyLoginListeners(DriveType type, BaseUser user) {
        for (AuthListener listener : this.listeners) {
            listener.register(type, user);
        }
    }

    protected void addListener(AuthListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
}
