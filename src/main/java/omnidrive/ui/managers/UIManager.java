package omnidrive.ui.managers;

import omnidrive.api.managers.AccountsManager;
import omnidrive.ui.accounts.AccountsFXML;
import omnidrive.ui.general.SyncProgress;

import java.net.URL;
import java.nio.file.Path;

public class UIManager {

    private boolean guiStarted = false;

    private final AccountsManager accountsManager;

    private final Path root;

    public UIManager(AccountsManager accountsManager, Path root) {
        this.accountsManager = accountsManager;
        this.root = root;
    }

    public void startGuiInBackground() {
        if (!guiStarted) {
            setup();
            loadHidden();
            guiStarted = true;
        }
    }

    public void startGuiInFront() {
        if (!guiStarted) {
            setup();
            loadShown();
            guiStarted = true;
        }
    }

    public void showGui() {
        AccountsFXML.show();
    }

    public void hideGui() {
        AccountsFXML.hide();
    }

    public void setSyncProgress(SyncProgress progress) {
        AccountsFXML.setSyncProgress(progress);
    }

    private void loadHidden() {
        AccountsFXML.load(accountsManager, true, root);
    }

    private void loadShown() {
        AccountsFXML.load(accountsManager, false, root);
    }

    private void setup() {
        String osname = System.getProperty("os.name").toLowerCase();
        if (osname.contains("mac")) {
            setupMacApp();
        } else if (osname.contains("windows")) {
            //setupWindowsApp();
        } else if (osname.contains("linux")) {
            //setupLinuxApp();
        }
    }

    private void setupMacApp() {
        // add dock icon
        URL iconURL = UIManager.class.getResource("/omnidrive_icon_1024.png");
        java.awt.Image image = new javax.swing.ImageIcon(iconURL).getImage();
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
    }

}
