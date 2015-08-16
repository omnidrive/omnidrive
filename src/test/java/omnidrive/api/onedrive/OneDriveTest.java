package omnidrive.api.onedrive;

import omnidrive.api.base.Account;

import omnidrive.api.base.AccountException;
import omnidrive.api.microsoft.OneDriveAccount;
import omnidrive.api.microsoft.lib.core.OneDriveCore;
import omnidrive.api.microsoft.lib.core.OneDriveOAuth;
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

    private static final String ONE_DRIVE_TOKEN = "EwBgAq1DBAAUGCCXc8wU/zFu9QnLdZXy+YnElFkAAekBfJwV8+prxtEKALN5KybCdBTsjQGrsWWNz7PLPwSHLv6OOtXZUoJcUAoyxttIoOmkNvR1b0KCV1zNB3F24pdHcl0ZthZfpjLTyzOAy16Hcxx+Cltb4gjw+ONSVFR5QWnfTIOB+AidamzJRXWX8scjb8qyUJH7+LW6VFyXZiIbs2tdzU1ENCowaKch6vVz2gzsklnajhgyx2dIAZni82LKwor5mS8Aikf13yCO0toR98zCYwK4p3Ics+R/lsNTG8S1lErlARDWQ3AuhrDMopajztYEWvfJayUr9rNv/kmsUqOclIXYKe5feVfB07G91LC6Nvjm3FcC4ruSl9Ny9lQDZgAACOVy7L6cwAC4MAGjglEglloTbDelMCRcUlzIjKsXxbZYkzwxp5bAalZCxzKQMgAyI/Y0XLWXHyGBm4TTDQrCQE3VEBfnKOXqVJ8lCbsBIN3y1w9QcI2U4Gvj9wzw6axiYk4PlZhtZ+fxKXohKxdM5iXdk+FvzELMprJdQonUg1Ej4wXw0V/XskAc9vtbXbnVQSo10qZZOMHytGpxLV2RDzpffzcI0vfjisjpGXbX+xwBToh7duUZmKh1CwDUcm7gSWaDj0EtGs6vGPCYGwnKFASFNGGn6LjZ+4wileu/4c9Sum09aTg4monw6HMEkjWPOxXbD5z6Ho3rYwg1Am5PcsXit8MriV5Npkec0XNfeZ+W87r0mswTy7QillJzbJpKzPJcNHA1Ulwjx/lllVMYzPK56CGazebXdvTRVgE=";

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
