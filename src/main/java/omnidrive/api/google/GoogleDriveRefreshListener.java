package omnidrive.api.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;

import java.io.IOException;

public class GoogleDriveRefreshListener implements CredentialRefreshListener {

    private GoogleDriveAccount account;

    public void setAccount(GoogleDriveAccount account) {
        this.account = account;
    }

    @Override
    public void onTokenResponse(Credential credential, TokenResponse tokenResponse) throws IOException {
        if (account != null) {
            account.onTokenResponse(credential, tokenResponse);
        }
    }

    @Override
    public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse) throws IOException {
        if (account != null) {
            account.onTokenErrorResponse(credential, tokenErrorResponse);
        }
    }
}
