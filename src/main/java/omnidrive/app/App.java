package omnidrive.app;

import omnidrive.api.auth.AuthTokens;
import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.ui.general.PopupView;

import java.util.Map;


public class App implements Runnable {

    private final FileSystem fileSystem;
    private final AccountsManager accountsManager = AccountsManager.getAccountsManager();

    public App(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void run() {
        restoreRegisteredAccounts();
        this.fileSystem.startSync();
    }

    public void restoreRegisteredAccounts() {
        // TODO - restore registered accounts
        /*try {
            Map<DriveType, AuthTokens> registeredAccounts = this.fileSystem.getRegisteredAccounts();
            this.accountsManager.createAndStoreAccounts(registeredAccounts);
        } catch (Exception ex) {
            PopupView.popup().error("Failed to restore registered accounts.");
        }*/
    }
}
