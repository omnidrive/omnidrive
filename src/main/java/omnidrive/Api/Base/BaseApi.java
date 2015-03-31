package omnidrive.Api.Base;

import javafx.scene.web.WebEngine;
import omnidrive.Api.managers.LoginManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseApi {

    protected LoginManager loginManager;

    protected final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    private final String appName;
    private final String appKey;
    private final String appSecret;

    public BaseApi(String appName, String appKey, String appSecret) {
        this.appName = appName;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public abstract void login(LoginManager loginManager) throws BaseException;


    public abstract void fetchAccessToken(WebEngine engine) throws BaseException;


    public String getName() {
        return this.appName;
    }

    protected String getAppKey() {
        return this.appKey;
    }

    protected String getAppSecret() {
        return this.appSecret;
    }

    protected void openAuthUrl(String authUrl) {
        this.loginManager.showLoginView(this, authUrl);
    }

    protected void notifyLoginListeners(Object property) {
        for (PropertyChangeListener listener : this.listeners) {
            listener.propertyChange(new PropertyChangeEvent(this, "login_succeed", null, property));
        }
    }

    protected void addListener(PropertyChangeListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
}
