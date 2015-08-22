package omnidrive.app;

import omnidrive.api.account.Account;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.filesystem.watcher.Handler;
import omnidrive.filesystem.watcher.Watcher;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.ManifestSync;
import omnidrive.manifest.mapdb.MapDbManifest;
import omnidrive.manifest.mapdb.MapDbManifestSync;
import omnidrive.sync.SyncHandler;
import omnidrive.sync.Synchronizer;
import omnidrive.sync.upload.StupidStrategyForDemo;
import omnidrive.sync.upload.UploadStrategy;
import omnidrive.sync.upload.Uploader;
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

    final private Synchronizer synchronizer;

    public App(FileSystem fileSystem, AccountsManager accountsManager, UIManager uiManager) {
        this.fileSystem = fileSystem;
        this.accountsManager = accountsManager;
        this.uiManager = uiManager;
        manifestContext = new ManifestContext();
        synchronizer = getSynchronizer();
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
        List<Account> registeredAccounts = getRegisteredAccounts();
        Account lruAccount = resolveLeastRecentlyUpdatedAccount(registeredAccounts);
        fullSync(lruAccount);
        registeredAccounts.remove(lruAccount);
        manifestContext.sync.uploadToAll(registeredAccounts);
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

    private List<Account> getRegisteredAccounts() throws Exception {
        Manifest manifest = manifestContext.manifest;
        accountsManager.restoreAccounts(manifest.getAccountsMetadata());
        return accountsManager.getActiveAccounts();
    }

    private Account resolveLeastRecentlyUpdatedAccount(List<Account> accounts) throws Exception {
        return accounts.get(0); // Ideally this would be resolved using last modified time
    }

    private void fullSync(Account account) throws Exception {
        Manifest manifest = manifestContext.sync.downloadFromAccount(account);
        synchronizer.fullSync(manifest);
    }

    private void startWatcherThread() throws Exception {
        Path root = fileSystem.getRootPath();
        ManifestSync manifestSync = manifestContext.sync;
        Manifest manifest = manifestContext.manifest;
        Handler handler = new SyncHandler(synchronizer, manifestSync, accountsManager);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        ManifestFilter filter = new ManifestFilter();
        Watcher watcher = new Watcher(watchService, handler, filter);
        watcher.registerRecursive(root);

        accountsManager.addObserver(new NewAccountObserver(manifest, manifestSync));

        Thread thread = new Thread(watcher);
        thread.setDaemon(true);
        thread.start();
    }

    private Synchronizer getSynchronizer() {
        Path root = fileSystem.getRootPath();
        Manifest manifest = manifestContext.manifest;
        UploadStrategy uploadStrategy = new StupidStrategyForDemo(accountsManager);
        Uploader uploader = new Uploader(uploadStrategy);
        return new Synchronizer(root, manifest, uploader, accountsManager);
    }

    private class ManifestContext {

        final private boolean exists;

        final private DB db;

        final private Manifest manifest;

        final private ManifestSync sync;

        public ManifestContext() {
            File file = fileSystem.getManifestFile();
            exists = fileSystem.manifestExists();
            db = MapDbUtils.createFileDb(file);
            manifest = new MapDbManifest(db);
            sync = new MapDbManifestSync(file, db);
        }

    }

}
