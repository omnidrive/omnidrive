package omnidrive.app;

import omnidrive.api.auth.AuthTokens;
import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.ui.general.PopupView;

import java.net.URL;
import java.util.Map;

public class App implements Runnable {

    private final FileSystem fileSystem;
    private final AccountsManager accountsManager = AccountsManager.getAccountsManager();

    public App(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        setupApp();
    }

    public void run() {
        restoreRegisteredAccounts();
        this.fileSystem.startSync();
    }

    public void restoreRegisteredAccounts() {
        try {
            Map<DriveType, AuthTokens> registeredAccounts = this.fileSystem.getRegisteredAccounts();
            this.accountsManager.restoreAccounts(registeredAccounts);
        } catch (Exception ex) {
            PopupView.popup().error("Failed to restore registered accounts.");
        }
    }

    private void setupApp() {
        String osname = System.getProperty("os.name").toLowerCase();
        if (osname.contains("mac")) {
            setupMacApp();
        } else if (osname.contains("windows")) {
            setupWindowsApp();
        } else if (osname.contains("linux")) {
            setupLinuxApp();
        }
    }

    private void setupMacApp() {
        // add dock icon
        URL iconURL = App.class.getResource("/omnidrive_icon_1024.png");
        java.awt.Image image = new javax.swing.ImageIcon(iconURL).getImage();
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
    }

    private void setupWindowsApp() {

    }

    private void setupLinuxApp() {

    }
}
