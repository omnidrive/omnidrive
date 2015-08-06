package omnidrive.api.dropbox;

import com.dropbox.core.DbxRequestConfig;
import omnidrive.api.base.CloudAccount;

import omnidrive.api.base.AccountException;
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
import java.util.Locale;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DropboxTest {

    private static final String LOCAL_DOWNLOAD_PATH = "/Users/assafey/Downloads";

    private static final String DbxAccessToken = "-rySTYC5rUYAAAAAAAASsL7CbWbE1TIXg1rPGkLn7lAShpns-51ylA78jwy4DY7W";
    private static final DbxRequestConfig DbxConfig = new DbxRequestConfig("omnidrive", Locale.getDefault().toString());

    private static CloudAccount account = null;

    public DropboxTest() {

    }

    @Before
    public void setUp() throws Exception {
        if (account == null) {
            account = new DropboxAccount(DbxConfig, DbxAccessToken);
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
        assertFalse(exists);

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
