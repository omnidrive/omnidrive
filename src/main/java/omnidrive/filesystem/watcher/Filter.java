package omnidrive.filesystem.watcher;

import java.io.File;

public interface Filter {

    boolean shouldIgnore(File file);

}
