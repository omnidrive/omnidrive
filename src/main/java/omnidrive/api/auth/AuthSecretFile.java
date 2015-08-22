package omnidrive.api.auth;

import org.json.JSONObject;

import java.io.*;

public class AuthSecretFile {

    public static final String DEFAULT_AUTH_SECRET_FILE = AuthSecretFile.class.getResource("/accounts.secret").getPath();

    private JSONObject secretObject = null;

    public AuthSecretFile analyze() {
        return analyze(DEFAULT_AUTH_SECRET_FILE);
    }

    public AuthSecretFile analyze(String path) {
        if (!analyzed()) {
            try {
                File secretFile = new File(path);
                InputStream secretStream = new FileInputStream(secretFile);
                InputStreamReader streamReader = new InputStreamReader(secretStream);
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(streamReader);

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                secretObject = new JSONObject(stringBuilder.toString());
            } catch (Exception ex) {
                System.out.println("Failed to parse '" + path + "' file.");
            }
        }

        return this;
    }

    public String getSecret(AuthSecretKey key) {
        if (secretObject != null && secretObject.has(key.toString())) {
            return secretObject.getString(key.toString());
        } else {
            return null;
        }
    }

    private boolean analyzed() {
        return secretObject != null;
    }
}
