package omnidrive.api.dropbox;

import com.dropbox.core.*;
import javafx.scene.web.WebEngine;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.auth.AuthSecretKey;
import omnidrive.api.account.*;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DropboxAuthorizer extends AccountAuthorizer {

    // Dropbox App Keys
    private static final String APP_NAME = "Dropbox";
    private static final String APP_KEY = "xoqvidzofuotsju";
    private static final String REDIRECT_URI = "https://www.dropbox.com/";

    private final DbxAppInfo appInfo;
    private final DbxRequestConfig config;
    private final DbxSessionStore store;
    private final DbxWebAuth auth;

    public DropboxAuthorizer(AuthSecretFile secretFile) {
        super(APP_NAME, APP_KEY, secretFile, AuthSecretKey.Dropbox);

        this.config = new DbxRequestConfig("omnidrive", Locale.getDefault().toString());
        this.appInfo = new DbxAppInfo(getAppId(), getAppSecret());
        this.store = new DropboxSession();

        this.auth = new DbxWebAuth(this.config, this.appInfo, REDIRECT_URI, this.store);
    }

    @Override
    public Account restoreAccount(AccountMetadata metadata, RefreshedAccountObserver observer) throws AccountException {
        // dropbox do not revokes the access token, refresh not needed
        return new DropboxAccount(metadata, this.config);
    }

    @Override
    public final String authUrl() {
        return this.auth.start();
    }

    @Override
    public final Account authenticate(WebEngine engine) throws AccountException {
        Account account = null;

        String url = engine.getLocation();
        if (url.contains("?state=") && url.contains("&code=")) {
            int indexOfState = url.indexOf("?state=") + "?state=".length();
            int indexOfCode = url.indexOf("&code=") + "&code=".length();
            String state = url.substring(indexOfState, indexOfCode - "&code=".length());
            String code = url.substring(indexOfCode);
            account = createAccountFromAuthCode(code + "&" + state);
        }

        return account;
    }

    @Override
    public final Account createAccountFromAuthCode(String codeAndState) throws AccountException {
        DropboxAccount dbxAccount = null;

        //try {
        String[] params = codeAndState.split("&");
        if (params.length != 2) {
            throw new DropboxException("Failed to fetch code and state", null);
        }

        try {
            final String code = params[0];
            final String state = URLDecoder.decode(params[1], "UTF-8");

            Map<String, String[]> paramsMap = new HashMap<>();
            paramsMap.put("code", new String[] {code});
            paramsMap.put("state", new String[] {state});

            DbxAuthFinish authFinish = this.auth.finish(paramsMap);
            AccountMetadata metadata = new AccountMetadata(getAppId(), getAppSecret(), authFinish.accessToken, null);
            dbxAccount = new DropboxAccount(metadata, this.config);
        } catch (Exception ex) {
            throw new DropboxException("Failed to finish auth process.", ex);
        }

        return dbxAccount;
    }

    public DbxRequestConfig getConfig() {
        return this.config;
    }

    private class DropboxSession implements DbxSessionStore {

        private String session;

        @Override
        public String get() {
            return session;
        }

        @Override
        public void set(String s) {
            session = s;
        }

        @Override
        public void clear() {
            session = null;
        }
    }

}
