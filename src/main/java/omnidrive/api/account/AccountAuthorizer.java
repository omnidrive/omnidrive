package omnidrive.api.account;

import javafx.scene.web.WebEngine;
import omnidrive.api.auth.AuthListener;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.auth.AuthSecretKey;

import java.util.LinkedList;
import java.util.List;

public abstract class AccountAuthorizer {

    protected final List<AuthListener> listeners = new LinkedList<AuthListener>();

    private final String appName;
    private final String appId;
    private final AuthSecretFile secretFile;
    private final AuthSecretKey secretKey;


    public AccountAuthorizer(String appName, String appId, AuthSecretFile secretFile, AuthSecretKey secretKey) {
        this.appName = appName;
        this.appId = appId;
        this.secretFile = secretFile;
        this.secretKey = secretKey;
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
        return this.secretFile.getSecret(this.secretKey);
    }

    protected void notifyAll(Account account) {
        for (AuthListener listener : this.listeners) {
            listener.authSucceed(account);
        }
    }

    protected void addListener(AuthListener listener) {
        if (listener != null) {
            if (!this.listeners.contains(listener)) {
                this.listeners.add(listener);
            }
        }
    }

    public abstract Account restoreAccount(AccountMetadata metadata, RefreshedAccountObserver observer) throws AccountException;

    public abstract String authUrl();

    public abstract Account authenticate(WebEngine engine) throws AccountException;

    protected abstract Account createAccountFromAuthCode(String code) throws AccountException;

    public void finishAuthentication(Account account) throws AccountException {
        account.initialize();
        notifyAll(account);
    }
}
