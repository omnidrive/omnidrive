package omnidrive.ui.managers;

import omnidrive.api.managers.AccountsManager;
import omnidrive.ui.accounts.AccountsFXML;
import omnidrive.ui.general.SyncProgress;

import java.net.URL;
import java.nio.file.Path;

public class UIManager {

    private static boolean guiStarted = false;

    public static void startGuiInBackground(AccountsManager accountsManager, Path omniDriveFolderPath) {
        if (!guiStarted) {
            setup();
            loadHidden(accountsManager, omniDriveFolderPath);
            guiStarted = true;
        }
    }

    public static void startGuiInFront(AccountsManager accountsManager, Path omniDriveFolderPath) {
        if (!guiStarted) {
            setup();
            loadShown(accountsManager, omniDriveFolderPath);
            guiStarted = true;
        }
    }

    public static void showGui() {
        AccountsFXML.show();
    }

    public static void hideGui() {
        AccountsFXML.hide();
    }

    public static void setSyncProgress(SyncProgress progress) {
        AccountsFXML.setSyncProgress(progress);
    }

    private static void loadHidden(AccountsManager accountsManager, Path omniDriveFolderPath) {
        AccountsFXML.load(accountsManager, true, omniDriveFolderPath);
    }

    private static void loadShown(AccountsManager accountsManager, Path omniDriveFolderPath) {
        AccountsFXML.load(accountsManager, false, omniDriveFolderPath);
    }

    private static void setup() {
        String osname = System.getProperty("os.name").toLowerCase();
        if (osname.contains("mac")) {
            setupMacApp();
        } else if (osname.contains("windows")) {
            //setupWindowsApp();
        } else if (osname.contains("linux")) {
            //setupLinuxApp();
        }
    }

    private static void setupMacApp() {
        // add dock icon
        URL iconURL = UIManager.class.getResource("/omnidrive_icon_1024.png");
        java.awt.Image image = new javax.swing.ImageIcon(iconURL).getImage();
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
    }
}
