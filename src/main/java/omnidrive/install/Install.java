package omnidrive.install;

import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.ui.accounts.AccountsFXML;
import omnidrive.ui.general.PopupView;

import java.util.Map;


public class Install {

    private final FileSystem fileSystem;

    public Install(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void doFirstInstallation() {
        try {
            this.fileSystem.initialize();
            showAccountsView();
        } catch (Exception ex) {
            PopupView.popup().error(ex.getMessage());
        }
    }

    public void doFirstInstallationIfNeeded() {
        if (!this.fileSystem.isReady()) {
            doFirstInstallation();
        }
    }

    public void showAccountsView() {
        AccountsFXML.show();
    }

}
