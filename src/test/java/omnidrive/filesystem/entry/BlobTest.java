package omnidrive.filesystem.entry;

import com.google.common.io.CharStreams;
import omnidrive.filesystem.BaseTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class BlobTest extends BaseTest {

    @Test
    public void testCreateBlobFromFile() throws Exception {
        File file = getResource("hello.txt");
        Blob blob = new Blob(file);

        UUID.fromString(blob.getId());
        assertEquals("Hello World", inputStreamToString(blob.getInputStream()));
        assertEquals(11, blob.getSize());
    }

    @Test
    public void testCopyWithNewId() throws Exception {
        File file = getResource("hello.txt");
        Blob blob = new Blob(file);
        String newId = "new id";
        Blob newBlob = blob.copyWithNewId(newId);

        assertEquals(newId, newBlob.getId());
        assertEquals("Hello World", inputStreamToString(newBlob.getInputStream()));
        assertEquals(11, newBlob.getSize());
    }

    private String inputStreamToString(InputStream inputStream) throws IOException {
        return CharStreams.toString(new InputStreamReader(inputStream));
    }

}