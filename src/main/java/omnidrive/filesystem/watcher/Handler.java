package omnidrive.filesystem.watcher;

import java.io.File;

public interface Handler {

    String create(File file) throws Exception;

    String modify(File file) throws Exception;

    void delete(File file) throws Exception;

}
