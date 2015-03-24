package omnidrive.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class BlobTest {

    private Blob blob;

    @Before
    public void setUp() throws Exception {
        URL resource = getClass().getClassLoader().getResource("fixtures/text_file.txt");
        assert resource != null;
        File file = new File(resource.toURI());
        blob = new Blob(file);
    }

    @After
    public void tearDown() {
        blob = null;
    }

    @Test
    public void testGetType() {
        assertEquals(Object.Type.BLOB, blob.getType());
    }

    @Test
    public void testGetHash() {
        assertEquals(Hash.of("hello world"), blob.getHash());
    }

    @Test
    public void testWrite() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        blob.write(out);

        String contents =
                "blob 11\0" +
                "hello world";

        assertEquals(contents, out.toString());
    }

}