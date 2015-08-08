package omnidrive.ui.managers;

import omnidrive.api.managers.AccountsManager;
import omnidrive.ui.accounts.AccountsController;
import omnidrive.ui.accounts.AccountsFXML;
import omnidrive.ui.general.SyncProgress;

import java.net.URL;
import java.nio.file.Path;

public class UIManager {

    private boolean guiStarted = false;

    private final AccountsManager accountsManager;

    private AccountsController uiController;

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

    public void setController(AccountsController controller) {
        this.uiController = controller;
    }

    public void showGui() {
        if (this.uiController != null) {
            this.uiController.showStage();
        }
    }

    public void hideGui() {
        if (this.uiController != null) {
            this.uiController.hideStage();
        }
    }

    public void setSyncProgress(SyncProgress progress) {
        if (this.uiController != null) {
            this.uiController.setSyncProgress(progress);
        }
    }

    private void loadHidden() {
        boolean hidden  = true;
        AccountsFXML.load(this, accountsManager, hidden, root);
    }

    private void loadShown() {
        boolean hidden  = false;
        AccountsFXML.load(this, accountsManager, hidden, root);
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
