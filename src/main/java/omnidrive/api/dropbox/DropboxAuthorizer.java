package omnidrive.api.dropbox;

import com.dropbox.core.*;
import javafx.scene.web.WebEngine;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.auth.AuthSecretKey;
import omnidrive.api.account.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Locale;


public class DropboxAuthorizer extends AccountAuthorizer {

    // Dropbox App Keys
    private static final String APP_NAME = "Dropbox";
    private static final String APP_KEY = "xoqvidzofuotsju";


    private final DbxAppInfo appInfo;
    private final DbxRequestConfig config = new DbxRequestConfig("omnidrive", Locale.getDefault().toString());
    private final DbxWebAuthNoRedirect auth;

    public DropboxAuthorizer(AuthSecretFile secretFile) {
        super(APP_NAME, APP_KEY, secretFile, AuthSecretKey.Dropbox);
        this.appInfo = new DbxAppInfo(getAppId(), getAppSecret());
        this.auth = new DbxWebAuthNoRedirect(this.config, appInfo);
    }

    @Override
    public Account restoreAccount(AccountMetadata metadata) throws AccountException {
        return new DropboxAccount(metadata, this.config);
    }

    @Override
    public final String authUrl() {
        return this.auth.start();
    }

    @Override
    public final Account authenticate(WebEngine engine) throws AccountException {
        Account account = null;
        Document doc = engine.getDocument();

        if (doc != null) {
            Element element = doc.getElementById("auth-code");
            if (element != null) {
                String code = element.getTextContent().trim();
                if (code != null) {
                    account = createAccountFromAuthCode(code);
                }
            }
        }

        return account;
    }

    @Override
    public final Account createAccountFromAuthCode(String code) throws AccountException {
        DropboxAccount dbxAccount = null;

        try {
            DbxAuthFinish authFinish = this.auth.finish(code);
            AccountMetadata metadata = new AccountMetadata(getAppId(), getAppSecret(), authFinish.accessToken, null);
            dbxAccount = new DropboxAccount(metadata, this.config);
        } catch (DbxException ex) {
            throw new DropboxException("Failed to finish auth process.");
        }

        return dbxAccount;
    }

    public DbxRequestConfig getConfig() {
        return this.config;
    }

}
