package omnidrive.app;

import omnidrive.api.base.CloudAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.ManifestSync;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.filesystem.manifest.MapDbManifestSync;
import omnidrive.filesystem.sync.StupidStrategyForDemo;
import omnidrive.filesystem.sync.SyncHandler;
import omnidrive.filesystem.sync.UploadStrategy;
import omnidrive.filesystem.watcher.Handler;
import omnidrive.filesystem.watcher.Watcher;
import omnidrive.ui.managers.UIManager;
import omnidrive.util.MapDbUtils;
import org.mapdb.DB;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.List;

public class App {

    final private FileSystem fileSystem;

    final private AccountsManager accountsManager;

    final private UIManager uiManager;

    final private ManifestContext manifestContext;

    public App(FileSystem fileSystem, AccountsManager accountsManager, UIManager uiManager) {
        this.fileSystem = fileSystem;
        this.accountsManager = accountsManager;
        this.uiManager = uiManager;
        manifestContext = new ManifestContext();
    }

    public void start() throws Exception {
        if (isFirstRun()) {
            startFirstRun();
        } else {
            startSubsequentRun();
        }
    }

    private void startFirstRun() throws Exception {
        initFileSystem();
        startWatcherThread();
        openAccountsSelector();
        // TODO
    }

    private void startSubsequentRun() throws Exception {
        List<CloudAccount> registeredAccounts = getRegisteredAccounts();
        CloudAccount lruAccount = resolveLeastRecentlyUpdatedAccount(registeredAccounts);
        // TODO rewrite this
        fullSync(lruAccount);
        for (CloudAccount account : registeredAccounts) {
            if (account != lruAccount) {
                upstreamSync(account);
            }
        }
        startWatcherThread();
        openTrayIcon();
    }

    private void initFileSystem() {
    }

    private void openAccountsSelector() {
        uiManager.startGuiInFront();
    }

    private void openTrayIcon() {
        uiManager.startGuiInBackground();
    }

    private boolean isFirstRun() {
        return !manifestContext.exists;
    }

    private List<CloudAccount> getRegisteredAccounts() throws Exception {
        Manifest manifest = manifestContext.manifest;
        accountsManager.restoreAccounts(manifest.getAccountsMetadata());
        return accountsManager.getActiveAccounts();
    }

    private CloudAccount resolveLeastRecentlyUpdatedAccount(List<CloudAccount> accounts) throws Exception {
        long lruTime = 0;
        CloudAccount lruAccount = null;
        for (CloudAccount account : accounts) {
            long accountUpdateTime = getAccountUpdateTime(account);
            if (accountUpdateTime > lruTime) {
                lruTime = accountUpdateTime;
                lruAccount = account;
            }
        }
        return lruAccount;
    }

    private long getAccountUpdateTime(CloudAccount account) throws Exception {
        // TODO - fix hack
        return 1;
//        File tempFile = File.createTempFile("manifest", "db");
//        OutputStream outputStream = new FileOutputStream(tempFile);
//        account.downloadManifest(outputStream);
//        outputStream.close();
//        DB db = MapDbUtils.createFileDb(tempFile);
//        Manifest manifest = new MapDbManifest(db);
//        long updateTime = manifest.getUpdatedTime();
//        db.close();
//        assert tempFile.delete();
//        return updateTime;
    }

    private void fullSync(CloudAccount account) {
        // TODO
    }

    private void upstreamSync(CloudAccount account) {
        // TODO
    }

    private void startWatcherThread() throws Exception {
        Path root = fileSystem.getRootPath();
        UploadStrategy uploadStrategy = new StupidStrategyForDemo(accountsManager);
        ManifestSync manifestSync = manifestContext.sync;
        Manifest manifest = manifestContext.manifest;
        Handler handler = new SyncHandler(root, manifest, manifestSync, uploadStrategy, accountsManager);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        ManifestFilter filter = new ManifestFilter(manifestContext.file.getName());
        Watcher watcher = new Watcher(watchService, handler, filter);
        watcher.registerRecursive(root);

        accountsManager.addObserver(new NewAccountObserver(manifest, manifestSync, accountsManager));

        Thread thread = new Thread(watcher);
        thread.setDaemon(true);
        thread.start();
    }

    private class ManifestContext {

        final private boolean exists;

        final private File file;

        final private DB db;

        final private Manifest manifest;

        final private ManifestSync sync;

        public ManifestContext() {
            exists = fileSystem.manifestExists();
            file = fileSystem.getManifestFile();
            db = MapDbUtils.createFileDb(file);
            manifest = new MapDbManifest(db);
            sync = new MapDbManifestSync(file, db);
        }

    }

}
