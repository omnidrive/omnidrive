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
import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseException;
import omnidrive.api.base.DriveType;
import omnidrive.api.managers.LoginManager;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleDriveApi extends BaseApi {

    private static final String APP_NAME = "GoogleDrive";
    private static final String CLIENT_ID = "438388195219-sf38d0f4bbj4t9at3e9n72uup3cfsb8m.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "57T8iW2bKRFZJSiX69Dr4cQV";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private LoginManager loginManager;

    private final List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    private final HttpTransport httpTransport = new NetHttpTransport();
    private final JsonFactory jsonFactory = new JacksonFactory();

    private final GoogleAuthorizationCodeFlow auth;

    public GoogleDriveApi() {
        super(APP_NAME, CLIENT_ID, CLIENT_SECRET);

        this.auth = new GoogleAuthorizationCodeFlow.Builder(
                this.httpTransport, this.jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public final String authorize() {
        return this.auth.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    public final void fetchAuthCode(WebEngine engine) throws BaseException {
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

    public final void finishAuthProcess(String code) throws BaseException {
        try {
            GoogleTokenResponse response = this.auth.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
            GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

            //Create a new authorized API client
            Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();

            notifyAll(DriveType.GoogleDrive, new GoogleDriveAccount(service));
        } catch (IOException ex) {
            throw new GoogleDriveException("Failed to finish auth process.");
        }
    }
}
