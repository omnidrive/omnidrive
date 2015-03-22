package omnidrive;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        final WatchService watcher = FileSystems.getDefault().newWatchService();

//        final SimpleFileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
//            @Override
//            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
//            {
//                System.out.println(dir.toString());
//                dir.register(watcher,
//                        StandardWatchEventKinds.ENTRY_CREATE,
//                        StandardWatchEventKinds.ENTRY_DELETE,
//                        StandardWatchEventKinds.ENTRY_MODIFY);
//
//                return FileVisitResult.CONTINUE;
//            }
//        };
//
//        Files.walkFileTree(new File(pathname).toPath(), fileVisitor);

        String pathname = "/home/amitayh/Desktop";
        Path dir = new File(pathname).toPath();

        dir.register(watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        boolean valid = true;

        while (valid) {
            WatchKey watckKey = watcher.take();

            List<WatchEvent<?>> events = watckKey.pollEvents();
            for (WatchEvent event : events) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    System.out.println("Created: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("Delete: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("Modify: " + event.context().toString());
                }
            }

            valid = watckKey.reset();
        };

        System.out.println("Ended");

    }

}
