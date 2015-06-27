package omnidrive;

import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import omnidrive.api.auth.AuthTokens;
import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.install.Install;
import omnidrive.ui.accounts.AccountsFXML;
import omnidrive.ui.general.MainFXML;
import omnidrive.ui.general.PopupView;
import omnidrive.ui.nsmenufx.NSMenuBarAdapter;
import omnidrive.ui.nsmenufx.convert.ToJavaFXConverter;

import java.net.URL;
import java.util.Map;


public class OmniDrive {

    private final FileSystem fileSystem;
    private final Install install;
    private final AccountsManager accountsManager = AccountsManager.getAccountsManager();

    public OmniDrive() {
        this.fileSystem = new FileSystem();
        this.install = new Install(this.fileSystem);
    }

    public void start() {
        //MainFXML.run();

        //AccountsFXML.show();

        //restoreRegisteredAccounts();

        install.doFirstInstallationIfNeeded();
    }

    public static void main(String[] args) {
        OmniDrive app = new OmniDrive();
        app.start();
    }

    private void restoreRegisteredAccounts() {
        try {
            Map<DriveType, AuthTokens> registeredAccounts = this.fileSystem.getRegisteredAccounts();
            this.accountsManager.restoreAccounts(registeredAccounts);
        } catch (Exception ex) {
            PopupView.popup().error("Failed to restore registered accounts.");
        }
    }
}
