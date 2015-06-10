package omnidrive.filesystem.sync;

import com.google.common.io.CharStreams;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.BaseTest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SyncHandlerTest extends BaseTest {

    public static final String UPLOAD_ID = "new-id";

    private Storage manifest = mock(Storage.class);

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
    public void testCreateFileUploadsToAccountUsingStrategy() throws Exception {
        File file = getResource("hello.txt");
        ArgumentCaptor<String> nameArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> inputStreamArgument = ArgumentCaptor.forClass(InputStream.class);

        handler.create(file);

        verify(account).uploadFile(nameArgument.capture(), inputStreamArgument.capture(), eq(file.length()));
        assertValidUUID(nameArgument.getValue());
        assertEquals("Hello World", CharStreams.toString(new InputStreamReader(inputStreamArgument.getValue())));
    }

    @Test
    public void testCreateFileAddsToManifest() throws Exception {
        File file = getResource("hello.txt");
        Path path = Paths.get(file.toURI());

        handler.create(file);

        verify(manifest).put(new Blob(UPLOAD_ID, file.length(), account.getName()));
    }

    @Test
    public void testCreateBlobSyncsManifestToAllAccounts() throws Exception {
        File file = getResource("hello.txt");

        handler.create(file);

//        verify(manifest).sync(account);
    }

    @Test
    public void testCreateEmptyDirAddsToManifest() throws Exception {
        File dir = Files.createTempDirectory("empty").toFile();
        ArgumentCaptor<Tree> argument = ArgumentCaptor.forClass(Tree.class);

        handler.create(dir);

        verify(manifest).put(argument.capture());
        Tree tree = argument.getValue();
        assertValidUUID(tree.getId());
        assertTrue(tree.getItems().isEmpty());

        //noinspection ResultOfMethodCallIgnored
        dir.delete();
    }

    private void assertValidUUID(String id) {
        try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(id);
        } catch (Exception e) {
            fail("Invalid UUID: " + id);
        }
    }

}