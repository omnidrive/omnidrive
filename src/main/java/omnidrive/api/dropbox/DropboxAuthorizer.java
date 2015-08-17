package omnidrive.api.dropbox;

import com.dropbox.core.*;
import javafx.scene.web.WebEngine;
import omnidrive.api.base.Account;
import omnidrive.api.base.AccountAuthorizer;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Locale;


public class DropboxAuthorizer extends AccountAuthorizer {

    // Dropbox App Keys
    private static final String APP_NAME = "Dropbox";
    private static final String APP_KEY = "zkbnr6hfxzqgxx2";
    private static final String APP_SECRET = "bznl1kw27j9mrk4";


    private final DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    private final DbxRequestConfig config = new DbxRequestConfig("omnidrive", Locale.getDefault().toString());
    private final DbxWebAuthNoRedirect auth;

    public DropboxAuthorizer() {
        super(APP_NAME, APP_KEY, APP_SECRET);

        this.auth = new DbxWebAuthNoRedirect(this.config, appInfo);
    }

    @Override
    public Account recreateAccount(String accessToken, String refreshToken) throws AccountException {
        return new DropboxAccount(this.config, accessToken);
    }

    @Override
    public final String authUrl() {
        return this.auth.start();
    }

    @Override
    public final void fetchAuthCode(WebEngine engine) throws AccountException {
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

    @Override
    public final void finishAuthProcess(String code) throws AccountException {
        try {
            DbxAuthFinish authFinish = this.auth.finish(code);
            DropboxAccount dbxAccount = new DropboxAccount(this.config, authFinish.accessToken);
            dbxAccount.initialize();
            notifyAll(AccountType.Dropbox, dbxAccount);
        } catch (DbxException ex) {
            throw new DropboxException("Failed to finish auth process.");
        }
    }

    public DbxRequestConfig getConfig() {
        return this.config;
    }

}
