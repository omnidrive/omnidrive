package omnidrive;

import omnidrive.api.auth.AuthTokens;
import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.install.Installation;
import omnidrive.ui.general.PopupView;

import java.util.Map;


public class OmniDrive {

    private final FileSystem fileSystem;
    private final Installation installation;
    private final AccountsManager accountsManager = AccountsManager.getAccountsManager();

    public OmniDrive() {
        this.fileSystem = new FileSystem();
        this.installation = new Installation(this.fileSystem);
    }

    public void start() {
        this.installation.install();
    }

    public static void main(String[] args) {
        OmniDrive app = new OmniDrive();
        app.start();
    }

}
