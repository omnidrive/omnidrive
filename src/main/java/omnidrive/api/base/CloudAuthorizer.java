package omnidrive.api.base;

import omnidrive.api.auth.AuthListener;
import omnidrive.api.auth.Authorizer;

import java.util.LinkedList;
import java.util.List;

public abstract class CloudAuthorizer implements Authorizer {

    protected final List<AuthListener> listeners = new LinkedList<AuthListener>();

    private final String appName;
    private final String appId;
    private final String appSecret;

    public CloudAuthorizer(String appName, String appId, String appSecret) {
        this.appName = appName;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public String login(AuthListener listener) throws AccountException {
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

    protected void notifyAll(AccountType type, CloudAccount account) {
        for (AuthListener listener : this.listeners) {
            listener.authenticated(type, account);
        }
    }

    protected void addListener(AuthListener listener) {
        if (listener != null) {
            if (!this.listeners.contains(listener)) {
                this.listeners.add(listener);
            }
        }
    }
}
