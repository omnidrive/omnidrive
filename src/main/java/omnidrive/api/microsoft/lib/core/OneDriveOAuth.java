package omnidrive.api.microsoft.lib.core;

import org.json.JSONObject;

public class OneDriveOAuth {
    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;
    private final String clientId;
    private final String clientSecret;

    public OneDriveOAuth(String clientId, String clientSecret, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.expiresIn = 1;
    }

    public OneDriveOAuth(String clientId, String clientSecret, JSONObject json) {
        this.accessToken = json.getString("access_token");
        this.refreshToken = json.getString("refresh_token");
        this.expiresIn = json.getLong("expires_in");
        this.clientId = clientId;
        this.clientSecret = clientSecret;
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
}
