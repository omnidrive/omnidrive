package omnidrive.ui.general;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.FileSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Created by assafey on 6/29/15.
 */
public class OmniDriveTrayIcon {

    private final Stage stage;
    private TrayIcon trayIcon;
    private boolean isShown;
    private MenuItem showItem;
    private MenuItem progressItem;
    private final Path omniDriveFolderPath;
    private AccountsManager accountsManager = null;

    public OmniDriveTrayIcon(Stage stage, Path omniDriveFolderPath) {
        this.stage = stage;
        this.isShown = false;
        this.omniDriveFolderPath = omniDriveFolderPath;
        Platform.setImplicitExit(false);
    }

    public void setAccountsManager(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public void applyStyle(boolean stageShown) throws Exception {
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            final SystemTray tray = SystemTray.getSystemTray();

            // load an image
            URL url = getClass().getResource("/omnidrive_trayicon.png");
            java.awt.Image image = ImageIO.read(url);

            this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent t) {
                    hide(stage);
                }
            });

            // create a action listener to listen for default action executed on the tray icon
            final ActionListener closeListener = new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    tray.remove(trayIcon);
                    System.exit(0);
                }
            };

            ActionListener showListener = new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            if (isShown) {
                                hide(stage);
                            } else {
                                show(stage);
                            }
                        }
                    });
                }
            };

            ActionListener folderListener = new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            try {
                                Desktop.getDesktop().open(omniDriveFolderPath.toFile());
                            } catch (IOException ex) {
                                PopupView.popup().error("Failed to open main folder.");
                            }
                        }
                    });
                }
            };

            ActionListener clearListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            try {
                                if (accountsManager != null) {
                                    accountsManager.clearAll();
                                }
                                System.exit(0);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                PopupView.popup().error("Failed to clear.");
                            }
                        }
                    });
                }
            };

            // create a popup menu
            PopupMenu popup = new PopupMenu();

            this.isShown = stageShown;
            if (stageShown) {
                this.showItem = new MenuItem("Hide");
            } else {
                this.showItem = new MenuItem("Show");
            }
            this.showItem.addActionListener(showListener);
            popup.add(this.showItem);



            MenuItem folderItem = new MenuItem("Open Folder");
            folderItem.addActionListener(folderListener);
            popup.add(folderItem);

            this.progressItem = new MenuItem(SyncProgress.Ready.toString());
            this.progressItem.setEnabled(false);
            popup.add(this.progressItem);

            MenuItem clearItem = new MenuItem("Reset OmniDrive");
            clearItem.addActionListener(clearListener);
            popup.add(clearItem);

            MenuItem closeItem = new MenuItem("Quit");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);

            // construct a TrayIcon
            this.trayIcon = new TrayIcon(image, "OmniDrive", popup);
            // set the TrayIcon properties
            this.trayIcon.addActionListener(showListener);

            tray.add(this.trayIcon);
        }
    }

    private void show(final Stage stage) {
        this.isShown = true;
        this.showItem.setLabel("Hide");
        stage.show();
        stage.toFront();
    }

    private void hide(final Stage stage) {
        this.isShown = false;
        this.showItem.setLabel("Show");
        stage.hide();
    }

    public void setProgress(SyncProgress progress) {
        this.progressItem.setLabel(progress.toString());
    }
}
