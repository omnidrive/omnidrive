package omnidrive.api.base;

import java.io.Serializable;

public class AccountMetadata implements Serializable {

    private String accessToken;

    private String manifestId;

    public AccountMetadata() {
        this.accessToken = null;
        this.manifestId = null;
    }

    public AccountMetadata(String accessToken, String manifestId) {
        this.accessToken = accessToken;
        this.manifestId = manifestId;
    }

    public String getManifestId() {
        return manifestId;
    }

    public void setManifestId(String manifestId) {
        this.manifestId = manifestId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
