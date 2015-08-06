package omnidrive.app;

import omnidrive.api.base.Account;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.ui.managers.UIManager;
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
        //startWatcherThread();
        openAccountsSelector();
        // TODO
    }

    private void startSubsequentRun() throws Exception {
        List<Account> registeredAccounts = getRegisteredAccounts();
        Account lruAccount = resolveLeastRecentlyUpdatedAccount(registeredAccounts);
        // TODO rewrite this
        fullSync(lruAccount);
        for (Account account : registeredAccounts) {
            if (account != lruAccount) {
                upstreamSync(account);
            }
        }
        startWatcherThread();
    }

    private void initFileSystem() {
    }

    private void openAccountsSelector() {
        UIManager.startGuiInFront(accountsManager, FileSystem.getRootPath());
    }

    private boolean isFirstRun() {
        return true;
        //return !fileSystem.manifestExists();
    }

    private List<Account> getRegisteredAccounts() throws Exception {
        Manifest manifest = fileSystem.getManifest();
        accountsManager.restoreAccounts(manifest.getAccountsMetadata());
        return accountsManager.getActiveAccounts();
    }

    private Account resolveLeastRecentlyUpdatedAccount(List<Account> accounts) throws Exception {
        long lruTime = 0;
        Account lruAccount = null;
        for (Account account : accounts) {
            long accountUpdateTime = getAccountUpdateTime(account);
            if (accountUpdateTime > lruTime) {
                lruTime = accountUpdateTime;
                lruAccount = account;
            }
        }
        return lruAccount;
    }

    private long getAccountUpdateTime(Account account) throws Exception {
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

    private void fullSync(Account account) {
//        Syncer syncer = new Syncer(FileSystem.getRootPath(), account);
//        syncer.fullSync();
    }

    private void upstreamSync(Account account) {

    }

    private void startWatcherThread() {
    }

}
