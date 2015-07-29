package omnidrive;

import omnidrive.api.managers.AccountsManager;
import omnidrive.app.App;
import omnidrive.filesystem.FileSystem;
import omnidrive.ui.managers.UIManager;

import java.nio.file.Path;

public class OmniDrive {

    public static void main(String[] args) {
        try {
            Path root = FileSystem.defaultRootPath();
            FileSystem fileSystem = new FileSystem(root);
            AccountsManager accountsManager = new AccountsManager();
            UIManager uiManager = new UIManager(accountsManager, root);
            App app = new App(fileSystem, accountsManager, uiManager);
            app.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
