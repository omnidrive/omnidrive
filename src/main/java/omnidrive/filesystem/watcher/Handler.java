package omnidrive.filesystem.watcher;

import java.io.File;

public interface Handler {

    void create(File file);

    void modify(File file);

    void delete(File file);

}
