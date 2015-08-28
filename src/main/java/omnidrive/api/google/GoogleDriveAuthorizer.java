package omnidrive.api.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import javafx.scene.web.WebEngine;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.auth.AuthSecretKey;
import omnidrive.api.account.*;

import java.io.IOException;
import java.util.Arrays;

public class GoogleDriveAuthorizer extends AccountAuthorizer {

    private static final String APP_NAME = "GoogleDrive";
    private static final String CLIENT_ID = "438388195219-sf38d0f4bbj4t9at3e9n72uup3cfsb8m.apps.googleusercontent.com";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private final HttpTransport httpTransport = new NetHttpTransport();
    private final JsonFactory jsonFactory = new JacksonFactory();

    private final GoogleAuthorizationCodeFlow auth;

    public GoogleDriveAuthorizer(AuthSecretFile secretFile) {
        super(APP_NAME, CLIENT_ID, secretFile, AuthSecretKey.GoogleDrive);

        this.auth = new GoogleAuthorizationCodeFlow.Builder(
                this.httpTransport,
                this.jsonFactory,
                getAppId(),
                getAppSecret(),
                Arrays.asList(DriveScopes.DRIVE)
        ).setAccessType("online").setApprovalPrompt("auto").build();
    }

    @Override
    public Account restoreAccount(AccountMetadata metadata, RefreshedAccountObserver observer) throws AccountException {
        GoogleCredential.Builder builder = new GoogleCredential.Builder();
        GoogleCredential credential = builder
                .setClientSecrets(getAppId(), getAppSecret())
                .setJsonFactory(jsonFactory)
                .setTransport(httpTransport)
                .build();

        credential.setAccessToken(metadata.getAccessToken());
        credential.setRefreshToken(metadata.getRefreshToken());

        //Create a new authorized API client
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("omnidrive").build();

        GoogleDriveAccount account = new GoogleDriveAccount(metadata, service, observer);
        builder.addRefreshListener(account);

        account.refreshAuthorization(credential);

        return account;
    }

    @Override
    public final String authUrl() {
        return this.auth.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    @Override
    public final Account authenticate(WebEngine engine) throws AccountException {
        Account account = null;
        String code = null;

        String title = engine.getTitle();
        if (title != null) {
            if (title.contains("Success code")) {
                try {
                    code = title.split("=")[1].trim();
                } catch (Exception ex) {
                    code = null;
                }
            }
        }

        if (code != null) {
            account = createAccountFromAuthCode(code);
        }

        return account;
    }

    @Override
    public final Account createAccountFromAuthCode(String code) throws AccountException {
        GoogleDriveAccount googleAccount = null;

        try {
            GoogleTokenResponse response = this.auth.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
            GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

            //Create a new authorized API client
            Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("omnidrive").build();

            AccountMetadata metadata = new AccountMetadata(
                    getAppId(),
                    getAppSecret(),
                    credential.getAccessToken(),
                    credential.getRefreshToken()
            );

            googleAccount = new GoogleDriveAccount(metadata, service);
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to finish auth process.", ex);
        }

        return googleAccount;
    }
}
