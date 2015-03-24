package omnidrive.repository;

import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class HashTest {

    @Test
    public void testHashString() {
        Hash hash = Hash.of("hello world");
        assertEquals("2aae6c35c94fcfb415dbe95f408b9ce91ee846ed", hash.getValue());
    }

    @Test
    public void testHashFile() throws Exception {
        URL resource = getClass().getClassLoader().getResource("fixtures/text_file.txt");
        assert resource != null;
        File file = new File(resource.toURI());
        Hash hash = Hash.of(file);
        assertEquals("2aae6c35c94fcfb415dbe95f408b9ce91ee846ed", hash.getValue());
    }

    @Test
    public void testEquals() {
        Hash hash1 = Hash.of("foo");
        Hash hash2 = Hash.of("foo");
        Hash hash3 = Hash.of("bar");

        assertTrue(hash1.equals(hash2));
        assertFalse(hash1.equals(hash3));
    }

}