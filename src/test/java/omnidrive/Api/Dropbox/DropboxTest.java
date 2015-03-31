package omnidrive.Api.Dropbox;

import omnidrive.Api.Base.BaseApi;
import omnidrive.Api.Base.BaseFile;
import omnidrive.Api.Base.BaseUser;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.FileOutputStream;

import static org.junit.Assert.*;


public class DropboxTest {

    private static BaseApi dbxApi;
    private static BaseUser dbxUser;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

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
        BaseFile dbxFile = dbxUser.getFile("/personal/config.xml");

        assertNotNull(dbxFile);

        FileOutputStream outputFile = dbxUser.downloadFile(dbxFile.getPath(), "/Users/assafey/Documents/config.xml");

        assertNotNull(outputFile);
    }

    @Test
    public void testFileUpload() throws Exception {
        BaseFile dbxUploadedFile = dbxUser.uploadFile("/Users/assafey/Downloads/omni_drive.xml", "/personal/omni_drive.xml");

        assertNotNull(dbxUploadedFile);
    }
}
