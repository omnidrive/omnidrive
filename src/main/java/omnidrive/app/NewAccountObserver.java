package omnidrive.app;

import omnidrive.api.base.CloudAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.ManifestSync;

import java.util.Observable;
import java.util.Observer;

public class NewAccountObserver implements Observer {

    final private Manifest manifest;

    final private ManifestSync manifestSync;

    final private AccountsManager accountsManager;

    public NewAccountObserver(Manifest manifest, ManifestSync manifestSync, AccountsManager accountsManager) {
        this.manifest = manifest;
        this.manifestSync = manifestSync;
        this.accountsManager = accountsManager;
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            addAccount((CloudAccount) arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAccount(CloudAccount account) throws Exception {
        System.out.println("Account added " + account);
        manifest.put(accountsManager.toType(account), account.getMetadata());
        if (accountPreviouslyConnected(account)) {
//            fullSync(account);
        } else {
            uploadManifest(account);
        }
    }

    private boolean accountPreviouslyConnected(CloudAccount account) throws Exception {
        return false;
//            return account.manifestExists();
    }

    private void uploadManifest(CloudAccount account) throws Exception {
        System.out.println("Uploading " + account);
        manifestSync.uploadToAccount(account);
    }

}
