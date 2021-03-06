package omnidrive.sync;

import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.watcher.Handler;
import omnidrive.manifest.ManifestSync;

import java.io.File;

public class SyncHandler implements Handler {

    private final Synchronizer synchronizer;

    private final ManifestSync manifestSync;

    private final AccountsManager accountsManager;

    public SyncHandler(Synchronizer synchronizer,
                       ManifestSync manifestSync,
                       AccountsManager accountsManager) {
        this.manifestSync = manifestSync;
        this.accountsManager = accountsManager;
        this.synchronizer = synchronizer;

    }

    public String create(File file) throws Exception {
        String id = synchronizer.upload(file);
        if (id != null) {
            uploadManifestToAllAccounts();
        }
        return id;
    }

    public String modify(File file) throws Exception {
        String id = synchronizer.update(file);
        if (id != null) {
            uploadManifestToAllAccounts();
        }
        return id;
    }

    public void delete(File file) throws Exception {
        boolean deleted = synchronizer.delete(file);
        if (deleted) {
            uploadManifestToAllAccounts();
        }
    }

    private void uploadManifestToAllAccounts() throws Exception {
        manifestSync.uploadToAll(accountsManager.getActiveAccounts());
    }

}