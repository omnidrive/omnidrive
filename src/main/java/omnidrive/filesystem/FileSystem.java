package omnidrive.filesystem;

import omnidrive.api.base.AccountMetadata;
import omnidrive.api.base.AccountType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.ManifestSync;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.filesystem.manifest.MapDbManifestSync;
import omnidrive.filesystem.sync.SimpleUploadStrategy;
import omnidrive.filesystem.sync.SyncHandler;
import omnidrive.filesystem.sync.UploadStrategy;
import omnidrive.filesystem.watcher.Handler;
import omnidrive.filesystem.watcher.Watcher;
import omnidrive.util.MapDbUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

public class FileSystem {

    private static final String USER_HOME = System.getProperty("user.home");

    public static final String MANIFEST_FILENAME = ".manifest";

    private static final String ROOT_NAME = "OmniDrive";

//    final private AccountsManager accountsManager;

    private final Path root;

//    public FileSystem(AccountsManager accountsManager) {
//        this.accountsManager = accountsManager;
//    }

    public FileSystem(Path root) {
        this.root = root;
    }

    static public Path getRootPath() {
        return Paths.get(USER_HOME, ROOT_NAME);
    }

    public boolean isReady() {
        return false;
    }

    public void initialize() throws IOException {
        Path root = getRootPath();
//        Files.createDirectory(root);

        AccountsManager accountsManager = new AccountsManager();
        WatchService watchService = FileSystems.getDefault().newWatchService();
        File manifestFile = new File("/Users/amitayh/manifest");
        DB db = DBMaker.newFileDB(manifestFile).closeOnJvmShutdown().make();
        Manifest manifest = new MapDbManifest(db);
        ManifestSync manifestSync = new MapDbManifestSync(accountsManager, manifestFile, db);
        UploadStrategy uploadStrategy = new SimpleUploadStrategy(accountsManager);
        Handler handler = new SyncHandler(root, manifest, manifestSync, uploadStrategy, accountsManager);

        Watcher watcher = new Watcher(watchService, handler);
        watcher.registerRecursive(root);

        Thread thread = new Thread(watcher);
        thread.setDaemon(true);
        thread.start();

        accountsManager.addObserver(new Observer() {
            public void update(Observable o, Object arg) {

            }
        });
    }

    public void startSync() {

    }

    public Map<AccountType, AccountMetadata> getRegisteredAccounts() {
        return new TreeMap<AccountType, AccountMetadata>();
    }

    ///////////////////////////////////////////////////////////////////////////

    public boolean manifestExists() {
        File manifest = getManifestFile();
        return manifest.isFile();
    }

    public Manifest getManifest() {
        File manifest = getManifestFile();
        DB db = MapDbUtils.createFileDb(manifest);
        return new MapDbManifest(db);
    }

    private File getManifestFile() {
        return root.resolve(MANIFEST_FILENAME).toFile();
    }

}
