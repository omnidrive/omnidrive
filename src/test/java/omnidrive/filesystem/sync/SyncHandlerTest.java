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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SyncHandlerTest extends BaseTest {

    public static final String UPLOAD_ID = "new-id";

    private Manifest manifest = mock(Manifest.class);

    private BaseAccount account = mock(BaseAccount.class);

    private UploadStrategy uploadStrategy = mock(UploadStrategy.class);

    private AccountsManager accountsManager = mock(AccountsManager.class);

    private SyncHandler handler = new SyncHandler(manifest, uploadStrategy, accountsManager);

    @Before
    public void setUp() throws Exception {
        when(uploadStrategy.selectAccount()).thenReturn(account);
        when(accountsManager.getActiveAccounts()).thenReturn(Collections.singletonList(account));
        when(account.uploadFile(anyString(), any(InputStream.class), anyLong())).thenReturn(UPLOAD_ID);
    }

    @Test
    public void testCreateBlobUploadsToAccountUsingStrategy() throws Exception {
        Blob blob = createBlob();
        String originalId = blob.getId();

        handler.create(blob);
        verify(account).uploadFile(eq(originalId), any(InputStream.class), eq(blob.getSize()));
    }

    @Test
    public void testCreateBlobAddsToManifestWithNewId() throws Exception {
        Blob blob = createBlob();
        handler.create(blob);

        assertEquals(UPLOAD_ID, blob.getId());
        verify(manifest).add(account, blob);
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