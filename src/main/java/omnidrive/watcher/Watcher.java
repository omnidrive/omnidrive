package omnidrive.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Watcher implements Runnable {

    final private WatchService watchService;

    private boolean running = false;

    public Watcher(WatchService watchService) {
        this.watchService = watchService;
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
                WatchKey watchKey = watchService.take();

                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent event : events) {
                    Path watchedPath = (Path) watchKey.watchable();
                    Path target = (Path) event.context();
                    File file = new File(watchedPath.toString(), target.toString());
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        if (file.isDirectory()) {
                            System.out.println("Watching new dir");
                            register(file.toPath());
                        }
                        System.out.println("Created: " + file);
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("Delete: " + file);
                    }
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("Modify: " + file);
                    }
                }

                watchKey.reset();
            }
        } catch (Exception e) {
            running = false;
        }
    }

    private class Visitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            register(dir);
            return FileVisitResult.CONTINUE;
        }

    }

}
