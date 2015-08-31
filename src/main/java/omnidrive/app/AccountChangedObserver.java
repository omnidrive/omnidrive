package omnidrive.app;

import omnidrive.api.account.Account;
import omnidrive.api.account.event.AccountEvent;
import omnidrive.api.account.event.AccountRefreshedEvent;
import omnidrive.api.account.event.AccountRemovedEvent;
import omnidrive.api.account.event.NewAccountAddedEvent;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.ManifestSync;

import java.util.Observable;
import java.util.Observer;

public class AccountChangedObserver implements Observer {

    final private Manifest manifest;

    final private ManifestSync manifestSync;

    public AccountChangedObserver(Manifest manifest, ManifestSync manifestSync) {
        this.manifest = manifest;
        this.manifestSync = manifestSync;
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            Account account = ((AccountEvent) arg).getAccount();
            if (arg instanceof NewAccountAddedEvent) {
                addAccount(account);
            } else if (arg instanceof AccountRefreshedEvent) {
                updateAccount(account);
            } else if (arg instanceof AccountRemovedEvent) {
                removeAccount(account);
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
        System.out.println("Account removed " + account);
        manifest.remove(account.getType());
    }

    private boolean accountPreviouslyConnected(Account account) throws Exception {
        return account.manifestExists();
    }

    private void uploadManifest(Account account) throws Exception {
        System.out.println("Uploading " + account);
        manifestSync.uploadToAccount(account);
    }

}
