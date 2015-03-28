package omnidrive.api.dropbox;

import com.dropbox.core.*;
import javafx.scene.web.WebEngine;
import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseException;
import omnidrive.api.managers.LoginManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DropboxApi implements BaseApi {

    // Dropbox App Keys
    private static final String APP_NAME = "Dropbox";
    private static final String APP_KEY = "zkbnr6hfxzqgxx2";
    private static final String APP_SECRET = "bznl1kw27j9mrk4";

    private LoginManager loginManager;

    private final DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    private final DbxRequestConfig config = new DbxRequestConfig("omnidrive", Locale.getDefault().toString());

    private DbxWebAuthNoRedirect auth;
    private final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    public DropboxApi() {
        this.auth = new DbxWebAuthNoRedirect(this.config, appInfo);
    }

    public void login(LoginManager loginManager) throws BaseException {
        addListener(loginManager);

        this.loginManager = loginManager;

        authorize();
    }

    public DbxRequestConfig getConfig() {
        return this.config;
    }

    public String getName() {
        return APP_NAME;
    }

    private void authorize() throws DropboxException {
        String authUrl = this.auth.start();
        openAuthUrl(authUrl);
    }

    private void openAuthUrl(String authUrl) {
        this.loginManager.showLoginView(this, authUrl);
    }

    public void authCompleted(WebEngine engine) {
        Document doc = engine.getDocument();
        String accessToken = findAccessToken(doc);
        if (accessToken != null) {
            notifyLoginListeners(accessToken);
        }
    }

    private String findAccessToken(Document doc) {
        String accessToken = null;

        if (doc != null) {
            Element element = doc.getElementById("auth-code");
            if (element != null) {
                String code = element.getTextContent().trim();
                if (code != null) {
                    try {
                        DbxAuthFinish authFinish = this.auth.finish(code);
                        accessToken = authFinish.accessToken;
                    } catch (DbxException ex) {
                        accessToken = null;
                    }
                }
            }
        }

        return accessToken;
    }

    private void notifyLoginListeners(String accessToken) {
        for (PropertyChangeListener listener : this.listeners) {
            listener.propertyChange(new PropertyChangeEvent(this, "access_token", null, accessToken));
        }
    }

    private void addListener(PropertyChangeListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
}
