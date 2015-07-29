package omnidrive.app;

import omnidrive.filesystem.watcher.Filter;

import java.io.File;

public class ManifestFilter implements Filter {

    final private String manifestFileName;

    public ManifestFilter(String manifestFileName) {
        this.manifestFileName = manifestFileName;
    }

    @Override
    public boolean shouldIgnore(File file) {
        return file.getName().startsWith(manifestFileName);
    }

}
