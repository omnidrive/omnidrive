package omnidrive.api.onedrive;

import omnidrive.api.base.Account;

import omnidrive.api.base.AccountException;
import omnidrive.api.microsoft.OneDriveAccount;
import omnidrive.api.microsoft.lib.core.OneDriveCore;
import omnidrive.api.microsoft.lib.auth.OneDriveOAuth;
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
public class OneDriveTest {

    private static final String LOCAL_DOWNLOAD_PATH = "/Users/assafey/Downloads";

    private static final String CLIENT_ID = "000000004C14C243";
    private static final String CLIENT_SECRET = "4Xucj-d2MSpbnxXJ8dbkhK3Bi1XWFUTC";

    private static final String ONE_DRIVE_TOKEN = "EwBoAq1DBAAUGCCXc8wU/zFu9QnLdZXy+YnElFkAAdCIyhzg7+1BVj6ZmNNs3TK0bTUjnQfYAfu3KawpI5r2c7WubNc5Jwa6pws5IYTENwa787eN/Uh6C5/HvWOQyLc3W5RLijO5nsyPl96ZEFsaa6ZFXrlhwtSPZU1co29/2yZ33d8yH6/uAlsAFGDXa2ne09jMpMsX1p4LOE5szUlEqchC5krITGkc3a+WDBJ/xGIoVf492Sr0Mf3gr+bI1VlzirtW9tf9xgykVZmY71sINrQY/DbHP3pvvBeYVdU7dN+yB3N0cFKRQ4E0qzaukhDYWSbwEoBc+wa4VedRtfosJHHvGy6K2xvjBIAj6qTTueCdO/hq0ueIQVuuQJo5ZRoDZgAACCfghAws1Qj3OAF6Ohp+OXGWxznKAjzF1p73vryAZrWcw6e3+Aer+RneoOeWTR9dvuUfuhHTtgA9vJsOXTleu3wb7IZS02egfzfmZwHdfc8Z6NllQHkm2m09pSfmDyE9h17AVHVd0H1Ls8QT587JcaF3LOCROTKZ8fRORwf+f17yUOQlH9S1+vM36m5W6uIjUks8jAkG4hPKKdk6ljxcJOT720sZPYCe5VScuugGtRRjYbe8aEV+Ko78MVPx0rBKlkmAsAJjFzLqXsMjJ+H8ZD/XRam5LRyvbwsuv/hW6k6ND7XaJT77lMODNeNqOEw3AZp/GkQBsGmx7VttNYjj93TbKVPb3S2BkBDD7vHfBupgAYjqKaKLfxnkTLxGe8anpcgz8A8kCnOpyxDNz2ERr6TN+Sc2YK725eFShTCGuiguUeRWAQ==";

    private static Account account = null;

    @Before
    public void setUp() throws Exception {
        if (account == null) {

            OneDriveCore core = new OneDriveCore(new OneDriveOAuth(CLIENT_ID, CLIENT_SECRET, ONE_DRIVE_TOKEN, null));
            account = new OneDriveAccount(core);

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
