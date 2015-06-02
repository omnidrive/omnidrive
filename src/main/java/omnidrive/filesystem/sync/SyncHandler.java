package omnidrive.filesystem.sync;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.watcher.Handler;

import java.io.File;

public class SyncHandler implements Handler {

    private final Manifest manifest;

    private final UploadStrategy uploadStrategy;

    private final AccountsManager accountsManager;

    @Inject
    public SyncHandler(Manifest manifest,
                       UploadStrategy uploadStrategy,
                       AccountsManager accountsManager) {
        this.manifest = manifest;
        this.uploadStrategy = uploadStrategy;
        this.accountsManager = accountsManager;
    }

    public void create(Blob blob) throws Exception {
        BaseAccount account = uploadStrategy.selectAccount();
        String newId = account.uploadFile(blob.getId(), blob.getInputStream(), blob.getSize());
        blob = blob.copyWithNewId(newId);
        manifest.add(account, blob);
        syncManifest();
    }

    public void modify(File file) throws Exception {

    }

    public void delete(File file) throws Exception {

    }

    private void syncManifest() {
        for (BaseAccount account : accountsManager.getActiveAccounts()) {
            manifest.sync(account);
        }
    }

}
