package omnidrive.filesystem.watcher;

import com.google.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Watcher implements Runnable {

    final private WatchService watchService;

    final private Handler handler;

    private boolean running = false;

    private WatchKey watchKey;

    @Inject
    public Watcher(WatchService watchService, Handler handler) {
        this.watchService = watchService;
        this.handler = handler;
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

    @Override
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

    private void pollEvents() throws InterruptedException, IOException {
        watchKey = watchService.take();
        List<WatchEvent<?>> events = watchKey.pollEvents();
        for (WatchEvent event : events) {
            handleEvent(event);
        }
        watchKey.reset();
    }

    private void handleEvent(WatchEvent event) throws IOException {
        File file = getFile(event);
        WatchEvent.Kind kind = event.kind();
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            if (file.isDirectory()) {
                registerRecursive(file.toPath());
            }
            handler.create(file);
        }
        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            handler.modify(file);
        }
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
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
