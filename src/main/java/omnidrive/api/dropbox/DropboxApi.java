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

    public final String authorize() {
        return this.auth.start();
    }

    public final void fetchAuthCode(WebEngine engine) throws BaseException {
        Document doc = engine.getDocument();

        if (doc != null) {
            Element element = doc.getElementById("auth-code");
            if (element != null) {
                String code = element.getTextContent().trim();
                if (code != null) {
                    finishAuthProcess(code);
                }
            }
        }
    }

    public final void finishAuthProcess(String code) throws BaseException {
        try {
            DbxAuthFinish authFinish = this.auth.finish(code);
            notifyLoginListeners(new DropboxUser(this.config, authFinish.accessToken));
        } catch (DbxException ex) {
            throw new DropboxException("Failed to finish auth process.");
        }
    }

    /*****************************************************************
     * Local methods
     *****************************************************************/

    public DbxRequestConfig getConfig() {
        return this.config;
    }

}
