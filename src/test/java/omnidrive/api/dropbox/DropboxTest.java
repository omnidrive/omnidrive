package omnidrive.api.dropbox;

import com.dropbox.core.DbxRequestConfig;
import omnidrive.api.base.BaseAccount;

import omnidrive.api.base.BaseApi;
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

    private static String uploadedFileId = "";

    private static BaseAccount dbxAccount = null;

    @Before
    public void setUp() {
        if (dbxAccount == null) {
            dbxAccount = new DropboxAccount(DbxConfig, DbxAccessToken);
        }
    }

    @After
    public void tearDown() {
        //this.dbxAccount = null;
    }

    @Test
    public void testA_Init() throws Exception {
        dbxAccount.initialize();
    }

    @Test
    public void testB_UploadFile() throws Exception {
        URL url = this.getClass().getResource("/api/upload_test.txt");
        File file = new File(url.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        uploadedFileId = dbxAccount.uploadFile("upload_test.txt", fileInputStream, file.length());

        assertNotNull(uploadedFileId);
        assertNotEquals(uploadedFileId, "");
    }

    @Test
    public void testC_DownloadFile() throws Exception {
        OutputStream outputStream = new FileOutputStream(LOCAL_DOWNLOAD_PATH + "/download_test.txt");
        long size = dbxAccount.downloadFile(uploadedFileId, outputStream);

        assertNotEquals(size, 0);
    }

    @Test
    public void testD_UpdateFile() throws Exception {
        URL url = this.getClass().getResource("/api/upload_test.txt");
        File file = new File(url.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        dbxAccount.updateFile(uploadedFileId, fileInputStream, file.length());
    }

    @Test
    public void textE_RemoveFile() throws Exception {
        dbxAccount.removeFile(uploadedFileId);
    }

    @Test
    public void testF_DownloadManifest() throws Exception {
        OutputStream outputStream = new FileOutputStream(LOCAL_DOWNLOAD_PATH + "/manifest");
        long size = dbxAccount.downloadManifestFile(outputStream);

        assertNotEquals(size, 0);
    }

    @Test
    public void testG_RestoreAccount() throws Exception {
        BaseApi api = new DropboxApi();
        dbxAccount = api.createAccount(DbxAccessToken);
        assertNotNull(dbxAccount);
    }
}
