package omnidrive.Dropbox;

import omnidrive.OmniBase.OmniApi;
import omnidrive.OmniBase.OmniFile;
import omnidrive.OmniBase.OmniFolder;
import omnidrive.OmniBase.OmniUser;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.FileOutputStream;
import java.util.List;

import static org.junit.Assert.*;


public class DropboxTest {

    private static OmniApi dbxApi;
    private static OmniUser dbxUser;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testLogin() throws Exception {
        dbxApi = new DropboxApi();

        dbxUser = dbxApi.login();

        assertNotNull(dbxUser);
    }

/*
    // TODO - listing folder recursively is too long....
    @Test
    public void testFoldersList() throws Exception {
        OmniFolder rootFolder = this.dbxUser.getFolder("/");

        assertNotNull(rootFolder);

        List<OmniFolder> folders = rootFolder.getFolders();

        assertNotNull(folders);

        final int MatchingFoldersInDrive = 3;
        int numOfMatchingFolders = 0;

        for (OmniFolder folder : folders) {
            if (folder.getName().equals("Apps") || folder.getName().equals("personal") || folder.getName().equals("photos")) {
                numOfMatchingFolders++;
            }
        }

        assertEquals(numOfMatchingFolders, MatchingFoldersInDrive);
    }
*/
    @Test
    public void testFileDownload() throws Exception {
        OmniFile dbxFile = dbxUser.getFile("/personal/config.xml");

        assertNotNull(dbxFile);

        FileOutputStream outputFile = dbxUser.downloadFile(dbxFile, "/Users/assafey/Documents/config.xml");

        assertNotNull(outputFile);
    }

    @Test
    public void testFileUpload() throws Exception {
        OmniFile dbxUploadedFile = dbxUser.uploadFile("/Users/assafey/Downloads/omni_drive.xml", "/personal/omni_drive.xml");

        assertNotNull(dbxUploadedFile);
    }
}
