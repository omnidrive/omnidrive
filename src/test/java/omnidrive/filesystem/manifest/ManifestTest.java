package omnidrive.filesystem.manifest;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.entry.BlobMetadata;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ManifestTest {

    @Test
    public void testAddBlobPutsMetadataInStorage() throws Exception {
        Storage storage = mock(Storage.class);
        Manifest manifest = new Manifest(storage);

        BaseAccount account = mock(BaseAccount.class);
        String accountName = "my-account";

        when(account.getName()).thenReturn(accountName);
        String content = "Hello World";
        Blob blob = new Blob("id", new ByteArrayInputStream(content.getBytes()), content.length());

        manifest.add(account, blob);
        verify(storage).put("id", new BlobMetadata(11, accountName));
    }

}