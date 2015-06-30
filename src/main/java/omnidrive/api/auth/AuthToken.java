package omnidrive.api.auth;

import java.io.Serializable;

public class AuthToken implements Serializable {

    private final String accessToken;

    private final String refreshToken;

    public AuthToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthToken authToken = (AuthToken) o;

        return accessToken.equals(authToken.accessToken) &&
                refreshToken.equals(authToken.refreshToken);

    }

    @Override
    public int hashCode() {
        int result = accessToken.hashCode();
        result = 31 * result + refreshToken.hashCode();
        return result;
    }

}
