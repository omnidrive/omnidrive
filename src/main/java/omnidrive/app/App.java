package omnidrive.app;

import omnidrive.api.account.Account;
import omnidrive.api.account.AccountMetadata;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.filesystem.watcher.DirWatcher;
import omnidrive.filesystem.watcher.Filter;
import omnidrive.filesystem.watcher.Handler;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.ManifestSync;
import omnidrive.manifest.mapdb.MapDbManifest;
import omnidrive.manifest.mapdb.MapDbManifestSync;
import omnidrive.sync.SyncHandler;
import omnidrive.sync.Synchronizer;
import omnidrive.sync.diff.DiffFilter;
import omnidrive.sync.upload.StupidStrategyForDemo;
import omnidrive.sync.upload.UploadStrategy;
import omnidrive.sync.upload.Uploader;
import omnidrive.ui.managers.UIManager;
import omnidrive.util.MapDbUtils;
import org.mapdb.DB;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
        registerToAccountChangedEvents();
        if (isFirstRun()) {
            startFirstRun();
        } else {
            startSubsequentRun();
        }
    }

    private void startFirstRun() throws Exception {
        ManifestFilter manifestFilter = new ManifestFilter();
        DiffFilter diffFilter = new DiffFilter();
        DirWatcher watcher = getWatcher(manifestFilter, diffFilter);
        startWatcherThread(watcher);
        openAccountsSelector();
    }

    private void startSubsequentRun() throws Exception {
        ManifestFilter manifestFilter = new ManifestFilter();
        DiffFilter diffFilter = new DiffFilter();
        DirWatcher watcher = getWatcher(manifestFilter, diffFilter);

        List<Account> registeredAccounts = getRegisteredAccounts();
        if (!registeredAccounts.isEmpty()) {
            Account lruAccount = resolveLeastRecentlyUpdatedAccount(registeredAccounts);
            fullSync(lruAccount, diffFilter);
            registeredAccounts.remove(lruAccount);
            manifestContext.sync.uploadToAll(registeredAccounts);
        }

        startWatcherThread(watcher);
        openTrayIcon();
    }

    private void registerToAccountChangedEvents() {
        ManifestSync manifestSync = manifestContext.sync;
        Manifest manifest = manifestContext.manifest;
        accountsManager.addObserver(new AccountChangedObserver(manifest, manifestSync));
    }

    private void openAccountsSelector() {
        uiManager.startGuiInFront();
    }

    private void openTrayIcon() {
        uiManager.startGuiInBackground();
    }

    private boolean isFirstRun() {
        boolean firstRun = false;

        if (!manifestContext.exists) {
            firstRun = true;
        } else {
            Manifest manifest = manifestContext.manifest;
            Map<String, AccountMetadata> accountsMetadata = manifest.getAccountsMetadata();
            firstRun = accountsMetadata.isEmpty();
        }

        return firstRun;
    }

    private List<Account> getRegisteredAccounts() throws Exception {
        Manifest manifest = manifestContext.manifest;
        accountsManager.restoreAccounts(manifest.getAccountsMetadata());
        return accountsManager.getActiveAccounts();
    }

    private Account resolveLeastRecentlyUpdatedAccount(List<Account> accounts) throws Exception {
        return accounts.get(0); // Ideally this would be resolved using last modified time
    }

    private void fullSync(Account account, DiffFilter filter) throws Exception {
        Manifest manifest = manifestContext.sync.downloadFromAccount(account);
        synchronizer.fullSync(manifest, filter);
    }

    private void startWatcherThread(DirWatcher watcher) throws Exception {
        Thread thread = new Thread(watcher);
        thread.setDaemon(true);
        thread.start();
    }

    private DirWatcher getWatcher(Filter... filters) throws Exception {
        Path root = fileSystem.getRootPath();
        ManifestSync manifestSync = manifestContext.sync;
        Handler handler = new SyncHandler(synchronizer, manifestSync, accountsManager);
        return new DirWatcher(root, handler, filters);
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
