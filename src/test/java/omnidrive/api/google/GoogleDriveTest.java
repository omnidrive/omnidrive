package omnidrive.api.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import omnidrive.api.auth.AuthSecretFile;
import omnidrive.api.auth.AuthSecretKey;
import omnidrive.api.account.Account;

import omnidrive.api.account.AccountException;
import omnidrive.api.account.AccountMetadata;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GoogleDriveTest {

    private static final String LOCAL_DOWNLOAD_PATH = "/Users/assafey/Downloads";

    private static final String CLIENT_ID = "438388195219-sf38d0f4bbj4t9at3e9n72uup3cfsb8m.apps.googleusercontent.com";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private static final String CLIENT_SECRET_FILE = GoogleDriveTest.class.getResource("/api/accounts.secret").getPath();
    private static final String TOKEN_SECRET_FILE = GoogleDriveTest.class.getResource("/api/tokens.secret").getPath();

    private static final AuthSecretFile clientSecretFile = new AuthSecretFile().analyze(CLIENT_SECRET_FILE);
    private static final AuthSecretFile tokenSecretFile = new AuthSecretFile().analyze(TOKEN_SECRET_FILE);


    private static Account account = null;

    @Before
    public void setUp() throws Exception {
        if (account == null) {
            final HttpTransport httpTransport = new NetHttpTransport();
            final JsonFactory jsonFactory = new JacksonFactory();

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setClientSecrets(CLIENT_ID, clientSecretFile.getSecret(AuthSecretKey.GoogleDrive))
                    .setJsonFactory(jsonFactory).setTransport(httpTransport).build()
                    .setAccessToken(tokenSecretFile.getSecret(AuthSecretKey.GoogleDrive));

            //Create a new authorized API client
            Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("omnidrive").build();

            AccountMetadata metadata = new AccountMetadata(
                    CLIENT_ID,
                    clientSecretFile.getSecret(AuthSecretKey.GoogleDrive),
                    tokenSecretFile.getSecret(AuthSecretKey.GoogleDrive),
                    null
            );

            account = new GoogleDriveAccount(metadata, service);

            try {
                account.initialize();
            } catch (AccountException ex) {
                account = null;
                throw new Exception("Failed to initialize account");
            }
        }
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testFileActions() throws Exception {
        // upload file
        URL url = this.getClass().getResource("/api/upload_test.txt");
        File file = new File(url.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        String uploadedFileId = account.uploadFile("upload_test.txt", fileInputStream, file.length());

        assertNotNull(uploadedFileId);
        assertNotEquals(uploadedFileId, "");

        // download file
        OutputStream outputStream = new FileOutputStream(LOCAL_DOWNLOAD_PATH + "/download_test.txt");
        long size = account.downloadFile(uploadedFileId, outputStream);

        assertNotEquals(size, 0);

        // update file
        url = this.getClass().getResource("/api/upload_test.txt");
        file = new File(url.getPath());
        fileInputStream = new FileInputStream(file);

        account.updateFile(uploadedFileId, fileInputStream, file.length());

        // remove file
        account.removeFile(uploadedFileId);
    }

    @Test
    public void testManifestActions() throws Exception {
        // manifest exists
        boolean exists = account.manifestExists();
        //assertFalse(exists);

        // upload manifest
        URL url = this.getClass().getResource("/api/manifest");
        File file = new File(url.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        account.uploadManifest(fileInputStream, file.length());

        // download manifest
        exists = account.manifestExists();
        assertTrue(exists);

        OutputStream outputStream = new FileOutputStream(LOCAL_DOWNLOAD_PATH + "/manifest");
        long size = account.downloadManifest(outputStream);

        assertNotEquals(size, 0);

        // update manifest
        exists = account.manifestExists();
        assertTrue(exists);

        url = this.getClass().getResource("/api/manifest");
        file = new File(url.getPath());
        fileInputStream = new FileInputStream(file);

        account.updateManifest(fileInputStream, file.length());

        // remove manifest
        account.removeManifest();
    }
}
