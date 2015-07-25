package omnidrive;

import omnidrive.api.managers.AccountsManager;
import omnidrive.app.App;
import omnidrive.filesystem.FileSystem;

import java.nio.file.Path;

public class OmniDrive {

    public static void main(String[] args) {
        try {
            Path root = FileSystem.getRootPath();
            FileSystem fileSystem = new FileSystem(root);
            AccountsManager accountsManager = new AccountsManager();
            App app = new App(fileSystem, accountsManager);
            app.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
