package omnidrive.app;

import omnidrive.api.account.Account;
import omnidrive.api.account.AccountChangedEvent;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.ManifestSync;

import java.util.Observable;
import java.util.Observer;

// TODO - amitay, maybe change name of class to 'AccountChangedObserver'
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
            if (arg instanceof AccountChangedEvent) {
                AccountChangedEvent event = (AccountChangedEvent) arg;
                switch (event.getState()) {
                    case Added:
                        addAccount(event.getAccount());
                        break;
                    case Refreshed:
                        updateAccount(event.getAccount());
                        break;
                    case Removed:
                        removeAccount(event.getAccount());
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAccount(Account account) {
        System.out.println("Account refreshed " + account);
        manifest.put(account.getType(), account.getMetadata());
    }

    private void addAccount(Account account) throws Exception {
        System.out.println("Account added " + account);
        if (!accountPreviouslyConnected(account)) {
            uploadManifest(account); // First upload will update account metadata
        } // TODO else full sync
        manifest.put(account.getType(), account.getMetadata());
    }

    private void removeAccount(Account account) {
        // TODO - amitay, please support remove
        System.out.println("Account removed " + account);
    }

    private boolean accountPreviouslyConnected(Account account) throws Exception {
        return account.manifestExists();
    }

    private void uploadManifest(Account account) throws Exception {
        System.out.println("Uploading " + account);
        manifestSync.uploadToAccount(account);
    }

}
