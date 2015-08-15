package omnidrive.api.base;

import javafx.scene.web.WebEngine;
import omnidrive.api.auth.AuthListener;
import java.util.LinkedList;
import java.util.List;

public abstract class AccountAuthorizer {

    protected final List<AuthListener> listeners = new LinkedList<AuthListener>();

    private final String appName;
    private final String appId;
    private final String appSecret;

    public AccountAuthorizer(String appName, String appId, String appSecret) {
        this.appName = appName;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public String login(AuthListener listener) throws AccountException {
        addListener(listener);
        return authUrl();
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

    protected void notifyAll(AccountType type, Account account) {
        for (AuthListener listener : this.listeners) {
            listener.authSucceed(type, account);
        }
    }

    protected void addListener(AuthListener listener) {
        if (listener != null) {
            if (!this.listeners.contains(listener)) {
                this.listeners.add(listener);
            }
        }
    }

    public abstract Account recreateAccount(String accessToken) throws AccountException;

    public abstract String authUrl();

    public abstract void fetchAuthCode(WebEngine engine) throws AccountException;

    public abstract void finishAuthProcess(String code) throws AccountException;
}
