package omnidrive.install;

import omnidrive.filesystem.FileSystem;
import omnidrive.ui.accounts.AccountsFXML;
import omnidrive.ui.general.PopupView;


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
            PopupView.error(ex.getMessage());
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
