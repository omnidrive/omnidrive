package omnidrive.api.dropbox;

import com.dropbox.core.*;
import javafx.scene.web.WebEngine;
import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseException;
import omnidrive.api.managers.LoginManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Locale;


public class DropboxApi extends BaseApi {

    // Dropbox App Keys
    private static final String APP_NAME = "Dropbox";
    private static final String APP_KEY = "zkbnr6hfxzqgxx2";
    private static final String APP_SECRET = "bznl1kw27j9mrk4";


    private final DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    private final DbxRequestConfig config = new DbxRequestConfig("omnidrive", Locale.getDefault().toString());
    private final DbxWebAuthNoRedirect auth;

    public DropboxApi() {
        super(APP_NAME, APP_KEY, APP_SECRET);

        this.auth = new DbxWebAuthNoRedirect(this.config, appInfo);
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public final void login(LoginManager loginManager) throws BaseException {
        addListener(loginManager);

        this.loginManager = loginManager;

        String authUrl = this.auth.start();

        openAuthUrl(authUrl);
    }


    public final void fetchAccessToken(WebEngine engine) throws BaseException {
        Document doc = engine.getDocument();

        if (doc != null) {
            Element element = doc.getElementById("auth-code");
            if (element != null) {
                String code = element.getTextContent().trim();
                if (code != null) {
                    try {
                        DbxAuthFinish authFinish = this.auth.finish(code);
                        notifyLoginListeners(authFinish.accessToken);
                    } catch (DbxException ex) {
                        throw new DropboxException("Failed to finish auth process.");
                    }
                }
            }
        }
    }

    /*****************************************************************
     * Local methods
     *****************************************************************/

    public DbxRequestConfig getConfig() {
        return this.config;
    }

}
