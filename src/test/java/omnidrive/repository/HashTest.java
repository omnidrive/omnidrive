package omnidrive.repository;

import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class HashTest {

    @Test
    public void testHashString() throws Exception {
        Hash hash = new Hash("hello world");
        assertEquals("2aae6c35c94fcfb415dbe95f408b9ce91ee846ed", hash.getValue());
    }

    @Test
    public void testHashFile() throws Exception {
        URL resource = getClass().getClassLoader().getResource("fixtures/text_file.txt");
        assert resource != null;
        File file = new File(resource.toURI());
        Hash hash = new Hash(file);
        assertEquals("2aae6c35c94fcfb415dbe95f408b9ce91ee846ed", hash.getValue());
    }


}