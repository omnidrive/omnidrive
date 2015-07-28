package omnidrive.ui.managers;

import omnidrive.ui.accounts.AccountsFXML;

import java.net.URL;
import java.nio.file.Path;

public class UIManager {

    private static final boolean StartHidden = true;

    public static void startGuiInBackground(Path omniDriveFolderPath) {
        setup();
        AccountsFXML.show(StartHidden, omniDriveFolderPath);
    }

    public static void startGuiInFront(Path omniDriveFolderPath) {
        setup();
        AccountsFXML.show(!StartHidden, omniDriveFolderPath);
    }

    public static void showGui() {
        AccountsFXML.show();
    }

    public static void hideGui() {
        AccountsFXML.hide();
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
