package omnidrive.api.microsoft.lib.auth;

import org.json.JSONObject;

import java.util.Date;

public class OneDriveOAuth {
    private final static int DELTA_AUTH_EXPIRED = 60;

    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;
    private final String clientId;
    private final String clientSecret;
    private final Date authTimestamp;

    public OneDriveOAuth(String clientId, String clientSecret, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.expiresIn = 3600;

        this.authTimestamp = new Date();
    }

    public OneDriveOAuth(String clientId, String clientSecret, JSONObject json) {
        this.accessToken = json.getString("access_token");
        this.refreshToken = json.getString("refresh_token");
        this.expiresIn = json.getLong("expires_in");
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        this.authTimestamp = new Date();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public boolean hasExpired() {
        boolean expired = false;
        Date now = new Date();

        long diff = (now.getTime() - authTimestamp.getTime()) / 1000;
        if (diff > expiresIn - DELTA_AUTH_EXPIRED) {
            expired = true;
        }

        return expired;
    }
}
