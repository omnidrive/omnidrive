package omnidrive.api.account;

import java.io.Serializable;

public class AccountMetadata implements Serializable {

    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String refreshToken;
    private String manifestId;
    private String rootFolderId;

    public AccountMetadata(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessToken = null;
        this.refreshToken = null;
        this.manifestId = null;
        this.rootFolderId = null;
    }

    public AccountMetadata(String clientId, String clientSecret, String accessToken, String refreshToken) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.manifestId = null;
        this.rootFolderId = null;
    }

    public AccountMetadata(String clientId, String clientSecret, String accessToken,
                           String refreshToken, String manifestId, String rootFolderId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.manifestId = manifestId;
        this.rootFolderId = rootFolderId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getManifestId() {
        return manifestId;
    }

    public void setManifestId(String manifestId) {
        this.manifestId = manifestId;
    }

    public String getRootFolderId() {
        return rootFolderId;
    }

    public void setRootFolderId(String rootFolderId) {
        this.rootFolderId = rootFolderId;
    }
}
