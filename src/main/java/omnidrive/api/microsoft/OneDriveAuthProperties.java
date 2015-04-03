package omnidrive.api.microsoft;

public class OneDriveAuthProperties {

    private String token_type;


    private int expires_in;


    private String scope;


    private String access_token;


    private String refresh_token;


    private String user_id;


    public String getTokenType() {
        return this.token_type;
    }


    public void setTokenType(String token_type) {
        this.token_type = token_type;
    }


    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    public int getExpiresIn() {
        return this.expires_in;
    }

    public void setExpiresIn(int expires_in) {
        this.expires_in = expires_in;
    }


    public String getAccessToken() {
        return this.access_token;
    }


    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }


    public String getRefreshToken() {
        return this.refresh_token;
    }


    public void setRefreshToken(String refresh_token) {
        this.refresh_token = refresh_token;
    }


    public String getUserId() {
        return this.user_id;
    }


    public void setUserId(String user_id) {
        this.user_id = user_id;
    }
}
