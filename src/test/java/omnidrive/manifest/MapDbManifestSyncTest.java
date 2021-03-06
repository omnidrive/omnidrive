package omnidrive.manifest;

import omnidrive.api.account.Account;
import omnidrive.api.account.AccountType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.manifest.mapdb.MapDbManifestSync;
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
        Account account = mock(Account.class);
        when(account.getType()).thenReturn(AccountType.Dropbox);
        AccountsManager accountsManager = new AccountsManager();
        accountsManager.setAccount(account);

        // And a manifest syncer
        File dbFile = File.createTempFile("manifest", "db");
        DB db = MapDbUtils.createFileDb(dbFile);
        MapDbManifestSync manifestSync = new MapDbManifestSync(dbFile, db);

        // When I upload the manifest
        manifestSync.uploadToAll(accountsManager.getActiveAccounts());

        // Then DB file should be uploaded to accounts
        verify(account).updateManifest(any(InputStream.class), anyLong());
    }

}