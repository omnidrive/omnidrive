package omnidrive.Dropbox;

import com.dropbox.core.*;

import java.io.*;
import java.util.Locale;

/**
 * Created by assafey on 3/21/15.
 */
public class DropboxApi {

    private static final String AppKey = "zkbnr6hfxzqgxx2";
    private static final String AppSecret = "bznl1kw27j9mrk4";

    public static DropboxUser login() throws DropboxException {
        DbxRequestConfig config = new DbxRequestConfig("omnidrive", Locale.getDefault().toString());
        String accessToken = authorize(config);

        return new DropboxUser(config, accessToken);
    }

    private static String authorize(DbxRequestConfig config) throws DropboxException {
        String accessToken = null;
        DbxAppInfo dropboxAppInfo = new DbxAppInfo(AppKey, AppSecret);
        DbxWebAuthNoRedirect dropboxAuth = new DbxWebAuthNoRedirect(config, dropboxAppInfo);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String authUrl = dropboxAuth.start();

        try {
            String code = openAuthUrl(authUrl);
            DbxAuthFinish authFinish = dropboxAuth.finish(code);
            accessToken = authFinish.accessToken;
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }

        return accessToken;
    }

    private static String openAuthUrl(String authUrl) {
        String code = null;

        System.out.println("1. Go to: " + authUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");

        try {
            code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return code;
    }
}
