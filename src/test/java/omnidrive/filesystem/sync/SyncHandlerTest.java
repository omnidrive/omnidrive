package omnidrive.filesystem.sync;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.BaseTest;
import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.manifest.Manifest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class SyncHandlerTest extends BaseTest {

    private Manifest manifest = mock(Manifest.class);

    private BaseAccount account = mock(BaseAccount.class);

    private UploadStrategy uploadStrategy = mock(UploadStrategy.class);

    private AccountsManager accountsManager = mock(AccountsManager.class);

    private SyncHandler handler = new SyncHandler(manifest, uploadStrategy, accountsManager);

    @Before
    public void setUp() {
        when(uploadStrategy.selectAccount()).thenReturn(account);
        when(accountsManager.getActiveAccounts()).thenReturn(Collections.singletonList(account));
    }

    @Test
    public void testCreateBlobUploadsToAccountUsingStrategy() throws Exception {
        Blob blob = createBlob();

        handler.create(blob);
        verify(account).uploadFile(blob.getId(), blob.getInputStream(), blob.getSize());
    }

    @Test
    public void testCreateBlobAddsToManifestWithNewId() throws Exception {
        String newId = "new id";
        when(account.uploadFile(anyString(), any(InputStream.class), anyLong())).thenReturn(newId);

        Blob blob = createBlob();
        handler.create(blob);

        Blob newBlob = blob.copyWithNewId(newId);
        verify(manifest).add(newBlob);
    }

    @Test
    public void testCreateBlobSyncsManifestToAllAccounts() throws Exception {
        Blob blob = createBlob();
        handler.create(blob);

        verify(manifest).sync(account);
    }

    private Blob createBlob() throws URISyntaxException, FileNotFoundException {
        File file = getResource("hello.txt");
        return new Blob(file);
    }

}