package omnidrive.filesystem.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Watcher implements Runnable {

    final private WatchService watchService;

    final private Handler handler;

    private Filter filter;

    private boolean running = false;

    private WatchKey watchKey;

    public Watcher(WatchService watchService, Handler handler, Filter filter) {
        this.watchService = watchService;
        this.handler = handler;
        this.filter = filter;
    }

    public void registerRecursive(Path root) throws IOException {
        Files.walkFileTree(root, new Visitor());
    }

    public void register(Path path) throws IOException {
        path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public void run() {
        running = true;
        try {
            while (running) {
                pollEvents();
            }
        } catch (Exception e) {
            running = false;
        }
    }

    private void pollEvents() throws Exception {
        watchKey = watchService.take();
        List<WatchEvent<?>> events = watchKey.pollEvents();
        for (WatchEvent event : events) {
            handleEvent(event);
        }
        watchKey.reset();
    }

    private void handleEvent(WatchEvent event) throws Exception {
        File file = getFile(event);

        if (filter != null && filter.shouldIgnore(file)) {
            return;
        }

        WatchEvent.Kind kind = event.kind();
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            if (file.isDirectory()) {
                registerRecursive(file.toPath());
            }
            System.out.println("Create " + file);
            handler.create(file);
        }
        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            System.out.println("Modify " + file);
            handler.modify(file);
        }
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            System.out.println("Delete " + file);
            handler.delete(file);
        }
    }

    private File getFile(WatchEvent event) {
        Path watchedPath = (Path) watchKey.watchable();
        Path target = (Path) event.context();
        return new File(watchedPath.toString(), target.toString());
    }

    private class Visitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            register(dir);
            return FileVisitResult.CONTINUE;
        }

    }

}
