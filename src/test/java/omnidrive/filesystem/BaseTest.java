package omnidrive.filesystem;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

abstract public class BaseTest {

    protected File getResource(String name) throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource(name);
        assert url != null;
        return new File(url.toURI());
    }

}
