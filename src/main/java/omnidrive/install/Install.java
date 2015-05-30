package omnidrive.install;

import omnidrive.ui.accounts.AccountsFXML;

/**
 * Created by assafey on 5/30/15.
 */
public class Install {

    private final Filesystem filesystem;

    public Install(Filesystem filesystem) {
        this.filesystem = filesystem;
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
