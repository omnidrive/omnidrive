package omnidrive.app;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.util.MapDbUtils;
import org.mapdb.DB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class App {

    final private FileSystem fileSystem;

    final private AccountsManager accountsManager;

    public App(FileSystem fileSystem, AccountsManager accountsManager) {
        this.fileSystem = fileSystem;
        this.accountsManager = accountsManager;
    }

    public void start() throws Exception {
        if (isFirstRun()) {
            startFirstRun();
        } else {
            startSubsequentRun();
        }
    }

    private void startFirstRun() {
        initFileSystem();
        startWatcherThread();
        openAccountsSelector();
        // TODO
    }

    private void startSubsequentRun() throws Exception {
        List<BaseAccount> registeredAccounts = getRegisteredAccounts();
        BaseAccount lruAccount = resolveLeastRecentlyUpdatedAccount(registeredAccounts);
        // TODO rewrite this
        fullSync(lruAccount);
        for (BaseAccount account : registeredAccounts) {
            if (account != lruAccount) {
                upstreamSync(account);
            }
        }
        startWatcherThread();
    }

    private void initFileSystem() {
    }

    private void openAccountsSelector() {
    }

    private boolean isFirstRun() {
        return !fileSystem.manifestExists();
    }

    private List<BaseAccount> getRegisteredAccounts() throws Exception {
        Manifest manifest = fileSystem.getManifest();
        accountsManager.restoreAccounts(manifest.getAccountsMetadata());
        return accountsManager.getActiveAccounts();
    }

    private BaseAccount resolveLeastRecentlyUpdatedAccount(List<BaseAccount> accounts) throws Exception {
        long lruTime = 0;
        BaseAccount lruAccount = null;
        for (BaseAccount account : accounts) {
            long accountUpdateTime = getAccountUpdateTime(account);
            if (accountUpdateTime > lruTime) {
                lruTime = accountUpdateTime;
                lruAccount = account;
            }
        }
        return lruAccount;
    }

    private long getAccountUpdateTime(BaseAccount account) throws Exception {
        File tempFile = File.createTempFile("manifest", "db");
        OutputStream outputStream = new FileOutputStream(tempFile);
        // TODO use method from account
        account.downloadFile("manifest", outputStream);
        outputStream.close();
        DB db = MapDbUtils.createFileDb(tempFile);
        Manifest manifest = new MapDbManifest(db);
        long updateTime = manifest.getUpdatedTime();
        db.close();
        assert tempFile.delete();
        return updateTime;
    }

    private void fullSync(BaseAccount account) {
//        Syncer syncer = new Syncer(FileSystem.getRootPath(), account);
//        syncer.fullSync();
    }

    private void upstreamSync(BaseAccount account) {

    }

    private void startWatcherThread() {
    }

}
