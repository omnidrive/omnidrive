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
import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.CloudAuthorizer;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.AccountType;
import omnidrive.api.managers.LoginManager;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleDriveAuthorizer extends CloudAuthorizer {

    private static final String APP_NAME = "GoogleDrive";
    private static final String CLIENT_ID = "438388195219-sf38d0f4bbj4t9at3e9n72uup3cfsb8m.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "57T8iW2bKRFZJSiX69Dr4cQV";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private LoginManager loginManager;

    private final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    private final HttpTransport httpTransport = new NetHttpTransport();
    private final JsonFactory jsonFactory = new JacksonFactory();

    private final GoogleAuthorizationCodeFlow auth;

    public GoogleDriveAuthorizer() {
        super(APP_NAME, CLIENT_ID, CLIENT_SECRET);

        this.auth = new GoogleAuthorizationCodeFlow.Builder(
                this.httpTransport, this.jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();
    }

    @Override
    public CloudAccount createAccount(String accessToken) throws AccountException {
        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .setJsonFactory(jsonFactory)
                .setTransport(httpTransport).build()
                .setAccessToken(accessToken);

        //Create a new authorized API client
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("omnidrive").build();

        return new GoogleDriveAccount(service, accessToken);
    }

    @Override
    public final String authorize() {
        return this.auth.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    @Override
    public final void fetchAuthCode(WebEngine engine) throws AccountException {
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
            finishAuthProcess(code);
        }
    }

    @Override
    public final void finishAuthProcess(String code) throws AccountException {
        try {
            GoogleTokenResponse response = this.auth.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
            GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

            //Create a new authorized API client
            Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("omnidrive").build();
            GoogleDriveAccount googleAccount = new GoogleDriveAccount(service, credential.getAccessToken());
            googleAccount.initialize();
            notifyAll(AccountType.GoogleDrive, googleAccount);
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to finish auth process.");
        }
    }
}
