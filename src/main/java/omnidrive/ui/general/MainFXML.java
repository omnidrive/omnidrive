package omnidrive.ui.general;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import omnidrive.ui.nsmenufx.NSMenuBarAdapter;

import java.net.URL;

public class MainFXML extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        setup();
        stage.show();
    }

    public void setup() {
        String osname = System.getProperty("os.name").toLowerCase();
        if (osname.contains("mac")) {
            setupMacApp();
        } else if (osname.contains("windows")) {
            setupWindowsApp();
        } else if (osname.contains("linux")) {
            setupLinuxApp();
        }
    }

    private void setupMacApp() {
        // add dock icon
        URL iconURL = MainFXML.class.getResource("/omnidrive_icon_1024.png");
        java.awt.Image image = new javax.swing.ImageIcon(iconURL).getImage();
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
//
//        new JFXPanel();
//        // add system menu bar
//        NSMenuBarAdapter adapter = new NSMenuBarAdapter();
//        MenuBar menuBar = adapter.getMenuBar();
//        adapter.setMenuBar(menuBar);
//
//        // add about item
//        boolean aboutItemFound = false;
//        for (int menuIdx = 0; menuIdx < menuBar.getMenus().size() && !aboutItemFound; menuIdx++) {
//            Menu menu = menuBar.getMenus().get(menuIdx);
//            for (MenuItem item : menu.getItems()) {
//                if (item.getText().toLowerCase().contains("about")) {
//                    aboutItemFound = true;
//                    setAboutAction(item);
//                    break;
//                }
//            }
//        }
        // TODO - add to screen
    }

    private void setupWindowsApp() {
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(false);
        menuBar.getMenus().addAll(getMainMenu(), getMainMenu());
        // TODO - add to screen
    }

    private void setupLinuxApp() {
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        menuBar.getMenus().addAll(getMainMenu(), getMainMenu());
        // TODO - add to screen
    }

    private Menu getMainMenu() {
        Menu omniMenu = new Menu("OmniDrive");

        MenuItem prefItem = new MenuItem("Preferences");
        setPreferencesAction(prefItem);

        MenuItem exitItem = new MenuItem("Exit");
        setExitAction(exitItem);

        omniMenu.getItems().addAll(prefItem, exitItem);

        return omniMenu;
    }

    private Menu getHelpMenu() {
        Menu helpMenu = new Menu("Help");

        MenuItem aboutItem = new MenuItem("About");
        setAboutAction(aboutItem);

        helpMenu.getItems().addAll(aboutItem);

        return helpMenu;
    }

    private void setPreferencesAction(MenuItem item) {
        item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                preferences();
            }
        });
    }

    private void setExitAction(MenuItem item) {
        item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                exit();
            }
        });
    }

    private void setAboutAction(MenuItem item) {
        item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                about();
            }
        });
    }

    private void about() {
        PopupView.popup().info("About");
    }

    private void preferences() {

    }

    private void exit() {

    }

    public static void run() {
        launch(null);
    }
}
