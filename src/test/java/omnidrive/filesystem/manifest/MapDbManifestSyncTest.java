package omnidrive.filesystem.manifest;

import omnidrive.api.base.CloudAccount;
import omnidrive.api.base.AccountType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.util.MapDbUtils;
import org.junit.Test;
import org.mapdb.DB;

import java.io.File;
import java.io.InputStream;

import static org.mockito.Mockito.*;

public class MapDbManifestSyncTest {

    @Test
    public void testUploadManifestToAccounts() throws Exception {
        // Given an account
        CloudAccount account = mock(CloudAccount.class);
        AccountsManager accountsManager = new AccountsManager();
        accountsManager.setAccount(AccountType.Dropbox, account);

        // And a manifest syncer
        File dbFile = File.createTempFile("manifest", "db");
        DB db = MapDbUtils.createFileDb(dbFile);
        MapDbManifestSync manifestSync = new MapDbManifestSync(accountsManager, dbFile, db);

        // When I upload the manifest
        manifestSync.upload();

        // Then DB file should be uploaded to accounts
        verify(account).uploadFile(eq("manifest"), any(InputStream.class), eq(dbFile.length()));
    }

}