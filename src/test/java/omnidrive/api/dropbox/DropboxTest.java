package omnidrive.api.dropbox;

import com.dropbox.core.DbxRequestConfig;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseAccount;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Locale;

import static org.junit.Assert.*;


public class DropboxTest {

    private static final String DbxAccessToken = "-rySTYC5rUYAAAAAAAASMYqQ3DAEEHcjDEyJl4q_qXH-8A6Md7gquyCChGh3o0iE";
    private static final DbxRequestConfig DbxConfig = new DbxRequestConfig("omnidrive", Locale.getDefault().toString());

    private BaseAccount dbxAccount;

    @Before
    public void setUp() {
        this.dbxAccount = new DropboxAccount(DbxConfig, DbxAccessToken);
    }

    @After
    public void tearDown() {
        this.dbxAccount = null;
    }

    @Test
    public void testUploadFile() throws Exception {
        URL url = this.getClass().getResource("/upload_test.txt");

        BaseFile uploadedFile = this.dbxAccount.uploadFile(url.getPath(), "/personal/upload_test.txt");

        assertNotNull(uploadedFile);

        assertEquals(uploadedFile.getPath(), "/personal/upload_test.txt");
    }

    @Test
    public void testDownloadFile() throws Exception {
        FileOutputStream downloadedFile = this.dbxAccount.downloadFile("/personal/download_test.txt",
                                                                        "/Users/assafey/Downloads/dropbox_download_test.txt");

        assertNotNull(downloadedFile);
    }
}
