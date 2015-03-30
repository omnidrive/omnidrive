package omnidrive;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import omnidrive.watcher.Handler;
import omnidrive.watcher.Watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;

public class Main {

//    public static void main(String[] args) throws IOException {
//        String root = "/home/amitayh/Dropbox";
//        System.out.println("Scanning...");
//        Object foo = walk(new File(root));
//        System.out.println("Done");
//    }
//
//    private static Object walk(File file) throws IOException {
//        if (file.isDirectory()) {
//            List<TreeEntry> entries = new LinkedList<>();
//            File[] files = file.listFiles();
//            if (files != null) {
//                for (File child : files) {
//                    Object obj = walk(child);
//                    TreeEntry entry = new TreeEntry(obj.getType(), obj.getHash(), child.getName());
//                    entries.add(entry);
//                }
//            }
//            return new Tree(entries);
//        } else {
////            System.out.println("Added: " + file.getAbsolutePath());
//            return new Blob(file);
//        }
//    }

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

    public static void main(String[] args) throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Handler handler = new Handler() {
            @Override
            public void create(File file) {
                System.out.println("Create: " + file);
            }

            @Override
            public void modify(File file) {
                System.out.println("Modify: " + file);
            }

            @Override
            public void delete(File file) {
                System.out.println("Delete: " + file);
            }
        };
        Watcher watcher = new Watcher(watchService, handler);

        Path root = new File("/home/amitayh/Desktop").toPath();
        watcher.registerRecursive(root);

        watcher.run();
    }

}
