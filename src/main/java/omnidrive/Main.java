package omnidrive;

import com.sleepycat.je.*;
import omnidrive.repository.*;
import omnidrive.repository.Object;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String root = "/home/amitayh/Videos";
        walk(new File(root));
    }

    private static Object walk(File file) throws IOException {
        if (file.isDirectory()) {
            List<TreeEntry> entries = new LinkedList<>();
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    Object obj = walk(child);
                    TreeEntry entry = new TreeEntry(obj.getType(), obj.getHash(), child.getName());
                    entries.add(entry);
                }
            }
            return new Tree(entries);
        } else {
            return new Blob(file);
        }
    }

    public static void main3(String[] args) throws Exception {

        Environment myDbEnvironment = null;
        Database myDatabase = null;

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        File envHome = new File("repo.db");
        myDbEnvironment = new Environment(envHome, envConfig);

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        myDatabase = myDbEnvironment.openDatabase(null, "sampleDatabase", dbConfig);

        System.out.println("OK");
    }

    public static void main2(String[] args) throws IOException, InterruptedException {

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
