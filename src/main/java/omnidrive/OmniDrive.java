package omnidrive;

import com.ning.http.client.AsyncHttpClient;
import omnidrive.api.managers.AccountsManager;
import omnidrive.app.App;
import omnidrive.filesystem.FileSystem;
import omnidrive.ui.managers.UIManager;

//import org.apache.log4j.BasicConfigurator;

import java.nio.file.Path;

public class OmniDrive {

    public static void main(String[] args) {
        try {
            //BasicConfigurator.configure(); // solves log4j warnings, but com.ning.async-http-client starts printing logs like crazy

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
