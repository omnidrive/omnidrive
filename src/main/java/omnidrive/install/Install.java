package omnidrive.install;

import omnidrive.filesystem.FileSystem;
import omnidrive.ui.accounts.AccountsFXML;

/**
 * Created by assafey on 5/30/15.
 */
public class Install {

    private final FileSystem fileSystem;

    public Install(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public boolean isFirstInstallation() {
        return this.fileSystem.isReady();
    }

    public void doFirstInstallation() {
        try {

            this.fileSystem.initialize();

            showAccountsView();

        } catch (Exception ex) {

        }
    }

    public void doFirstInstallationIfNeeded() {
        if (isFirstInstallation()) {
            doFirstInstallation();
        }
    }

    public void showAccountsView() {
        AccountsFXML.launch(null);
    }

}
