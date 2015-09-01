package omnidrive.sync.diff;

import omnidrive.filesystem.watcher.Filter;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class DiffFilter implements Filter {

    private final Set<Path> pathFilterSet;

    public DiffFilter() {
        pathFilterSet = new HashSet<>();
    }

    @Override
    public boolean shouldIgnore(File file) {
        Path path = file.toPath();
        if (pathFilterSet.contains(path)) {
            pathFilterSet.remove(path);
            System.out.println("Filter: ignore change in folder by diff process [" + path.toString() + "]");
            return true;
        }

        return false;
    }

    public void update(Path path) {
        if (!pathFilterSet.contains(path)) {
            pathFilterSet.add(path);
        }
    }
}
