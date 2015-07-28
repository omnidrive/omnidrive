package omnidrive.api.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import omnidrive.api.base.BaseAccount;

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
    private static final String CLIENT_SECRET = "57T8iW2bKRFZJSiX69Dr4cQV";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private static final String GOOGLE_TOKEN = "ya29.vgGUd9Oz40XkUdwkaXzlZyUX2nU40g-9jdAkdamLIZ1fHgIH6_FDYWCTlSkXeW1Osk7O";

    private static String uploadedFileId = "";

    private static BaseAccount googleAccount = null;

    @Before
    public void setUp() {
        if (googleAccount == null) {
            final HttpTransport httpTransport = new NetHttpTransport();
            final JsonFactory jsonFactory = new JacksonFactory();

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .setJsonFactory(jsonFactory).setTransport(httpTransport).build()
                    .setAccessToken(GOOGLE_TOKEN);

            //Create a new authorized API client
            Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("omnidrive").build();

            googleAccount = new GoogleDriveAccount(service);
        }
    }

    @After
    public void tearDown() {
        //this.dbxAccount = null;
    }

    @Test
    public void testA_Init() throws Exception {
        googleAccount.initialize();
    }

    @Test
    public void testB_UploadFile() throws Exception {
        URL url = this.getClass().getResource("/api/upload_test.txt");
        File file = new File(url.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        uploadedFileId = googleAccount.uploadFile("upload_test.txt", fileInputStream, file.length());

        assertNotNull(uploadedFileId);
        assertNotEquals(uploadedFileId, "");
    }

    @Test
    public void testC_DownloadFile() throws Exception {
        OutputStream outputStream = new FileOutputStream(LOCAL_DOWNLOAD_PATH + "/download_test.txt");
        long size = googleAccount.downloadFile(uploadedFileId, outputStream);

        assertNotEquals(size, 0);
    }

    @Test
    public void testD_UpdateFile() throws Exception {
        URL url = this.getClass().getResource("/api/upload_test.txt");
        File file = new File(url.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        googleAccount.updateFile(uploadedFileId, fileInputStream, file.length());
    }

    @Test
    public void textE_RemoveFile() throws Exception {
        googleAccount.removeFile(uploadedFileId);
    }

    @Test
    public void testF_DownloadManifest() throws Exception {
        OutputStream outputStream = new FileOutputStream(LOCAL_DOWNLOAD_PATH + "/manifest");
        long size = googleAccount.downloadManifestFile(outputStream);

        assertNotEquals(size, 0);
    }

    @Test
    public void testG_RestoreAccount() throws Exception {
        // TODO - restore dropbox account
    }
}
