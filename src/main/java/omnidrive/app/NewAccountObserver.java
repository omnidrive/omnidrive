package omnidrive.app;

import omnidrive.api.account.Account;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.sync.ManifestSync;

import java.util.Observable;
import java.util.Observer;

public class NewAccountObserver implements Observer {

    final private Manifest manifest;

    final private ManifestSync manifestSync;

    public NewAccountObserver(Manifest manifest, ManifestSync manifestSync) {
        this.manifest = manifest;
        this.manifestSync = manifestSync;
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            addAccount((Account) arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAccount(Account account) throws Exception {
        System.out.println("Account added " + account);
        manifest.put(account.getType(), account.getMetadata());
        if (!accountPreviouslyConnected(account)) {
            uploadManifest(account);
        } // TODO else full sync
    }

    private boolean accountPreviouslyConnected(Account account) throws Exception {
        return account.manifestExists();
    }

    private void uploadManifest(Account account) throws Exception {
        System.out.println("Uploading " + account);
        manifestSync.uploadToAccount(account);
    }

}
