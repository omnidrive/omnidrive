package omnidrive.filesystem.sync;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.watcher.Handler;

import java.io.File;

public class SyncHandler implements Handler {

    private final Manifest manifest;

    private final UploadStrategy uploadStrategy;

    private final AccountsManager accountsManager = AccountsManager.getAccountsManager();

    public SyncHandler(Manifest manifest, UploadStrategy uploadStrategy) {
        this.manifest = manifest;
        this.uploadStrategy = uploadStrategy;
    }

    @Override
    public void create(File file) {
        if (file.isFile()) {
            createFile(file);
        }
    }

    @Override
    public void modify(File file) {

    }

    @Override
    public void delete(File file) {

    }

    private void createFile(File file) {
        BaseAccount account = uploadStrategy.selectAccount();

        manifest.add(file);
//            account.uploadFile();
        syncManifest();
    }

    private void syncManifest() {
        for (BaseAccount account : accountsManager.getActiveAccounts()) {
//            account.uploadFile()
        }
    }

}
