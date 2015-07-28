package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;
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
public class BoxTest {

    private static final String LOCAL_DOWNLOAD_PATH = "/Users/assafey/Downloads";

    private static final String BOX_TOKEN = "URPWMR8cpPrN4gyaXk8cgLKGrnq6koi7";

    private static final BoxAPIConnection conn = new BoxAPIConnection(BOX_TOKEN);

    private static String uploadedFileId = "";

    private static BaseAccount boxAccount = null;

    @Before
    public void setUp() {
        if (boxAccount == null) {
            boxAccount = new BoxAccount(conn);
        }
    }

    @After
    public void tearDown() {
        //this.boxAccount = null;
    }

    @Test
    public void testA_Init() throws Exception {
        boxAccount.initialize();
    }

    @Test
    public void testB_UploadFile() throws Exception {
        URL url = this.getClass().getResource("/api/upload_test.txt");
        File file = new File(url.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        uploadedFileId = boxAccount.uploadFile("upload_test.txt", fileInputStream, file.length());

        assertNotNull(uploadedFileId);
        assertNotEquals(uploadedFileId, "");
    }

    @Test
    public void testC_DownloadFile() throws Exception {
        OutputStream outputStream = new FileOutputStream(LOCAL_DOWNLOAD_PATH + "/download_test.txt");
        long size = boxAccount.downloadFile(uploadedFileId, outputStream);

        assertNotEquals(size, 0);
    }

    @Test
    public void testD_UpdateFile() throws Exception {
        URL url = this.getClass().getResource("/api/upload_test.txt");
        File file = new File(url.getPath());
        FileInputStream fileInputStream = new FileInputStream(file);

        boxAccount.updateFile(uploadedFileId, fileInputStream, file.length());
    }

    @Test
    public void textE_RemoveFile() throws Exception {
        boxAccount.removeFile(uploadedFileId);
    }

    @Test
    public void testF_DownloadManifest() throws Exception {
        OutputStream outputStream = new FileOutputStream(LOCAL_DOWNLOAD_PATH + "/manifest");
        long size = boxAccount.downloadManifestFile(outputStream);

        assertNotEquals(size, 0);
    }

    @Test
    public void testG_RestoreAccount() throws Exception {
        // TODO - restore box account
    }
}
