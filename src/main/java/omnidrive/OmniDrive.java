package omnidrive;

import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;
import omnidrive.install.Installation;

import java.nio.file.Path;

public class OmniDrive {

    private final FileSystem fileSystem;
    private final Installation installation;
    private final AccountsManager accountsManager = new AccountsManager();

    public OmniDrive() {
        Path root = FileSystem.getRootPath();
        this.fileSystem = new FileSystem(root);
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
