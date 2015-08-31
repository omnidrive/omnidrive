package omnidrive.filesystem.watcher;

import com.sun.nio.file.SensitivityWatchEventModifier;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class DirWatcher implements Runnable {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final Handler handler;
    private final Filter filter;
    private boolean stop = false;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, new WatchEvent.Kind[]{
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY,
        }, SensitivityWatchEventModifier.HIGH);

        Path prev = keys.get(key);
        if (prev == null) {
            System.out.format("register: %s\n", dir);
        } else {
            if (!dir.equals(prev)) {
                System.out.format("update: %s -> %s\n", prev, dir);
            }
        }

        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public DirWatcher(Path dir, Handler handler, Filter filter) throws IOException {
        this.handler = handler;
        this.filter = filter;

        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();

        System.out.format("Scanning %s ...\n", dir);
        registerAll(dir);
        System.out.println("Done.");
    }

    private void handleChange(Path path, WatchEvent<?> event) throws Exception {
        if (filter != null && filter.shouldIgnore(path.toFile())) {
            return;
        }

        // print out event
        System.out.format("%s: %s\n", event.kind().name(), path);

        File file = path.toFile();

        WatchEvent.Kind kind = event.kind();
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
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

    /**
     * Process all events for keys queued to the watcher
     */
    private void watch() {
        while (!stop) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                try {
                    handleChange(child, event);
                } catch (Exception ex) {
                    System.out.format("Failed to handle file change '%s'\n", child);
                    ex.printStackTrace();
                }

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        stop = false;
        watch();
    }
}