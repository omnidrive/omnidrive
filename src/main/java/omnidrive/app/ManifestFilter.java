package omnidrive.app;

import omnidrive.filesystem.FileSystem;
import omnidrive.filesystem.watcher.Filter;

import java.io.File;

public class ManifestFilter implements Filter {

    @Override
    public boolean shouldIgnore(File file) {
        String manifestFileName = FileSystem.MANIFEST_FILENAME;
        return file.getName().startsWith(manifestFileName);
    }

}
