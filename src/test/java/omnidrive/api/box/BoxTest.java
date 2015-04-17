package omnidrive.api.box;


import com.box.sdk.BoxAPIConnection;
import omnidrive.api.base.BaseFile;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.FileOutputStream;
import java.net.URL;

import static org.junit.Assert.*;

public class BoxTest {

    private static final String DEVELOPER_TOKEN = "u40j2wV4rHZQLPICBJlFaMiCGukNVJmw";

    private BoxAPIConnection connection;
    private BoxUser boxUser;

    private static String uploadFileId = null;

    @Before
    public void setUp() {
        this.connection = new BoxAPIConnection(DEVELOPER_TOKEN);
        this.boxUser = new BoxUser(this.connection, com.box.sdk.BoxUser.getCurrentUser(this.connection).getID());
    }

    @After
    public void tearDown() {
        this.boxUser = null;
    }

    @Test
    public void testUploadFile() throws Exception {
        URL url = this.getClass().getResource("/upload_test.txt");

        BaseFile uploadedFile = this.boxUser.uploadFile(url.getPath(), "/upload_test.txt");

        assertNotNull(uploadedFile);

        uploadFileId = uploadedFile.getPath();
    }

    @Test
    public void testDownloadFile() throws Exception {
        assertNotNull(uploadFileId);

        FileOutputStream downloadedFile = this.boxUser.downloadFile(uploadFileId, "/Users/assafey/Downloads/box_download_test.txt");

        assertNotNull(downloadedFile);
    }
}
