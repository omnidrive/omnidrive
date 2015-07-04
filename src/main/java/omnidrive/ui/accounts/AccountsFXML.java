package omnidrive.ui.accounts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import omnidrive.filesystem.FileSystem;
import omnidrive.ui.general.OmniDriveTrayIcon;
import omnidrive.ui.general.SyncProgress;

import java.io.InputStream;
import java.net.URL;

public class AccountsFXML extends Application {

    public static FXMLLoader fxmlLoader;
    private static final String SCREEN_FXML_PATH = "/AccountsScreen.fxml";

    private AccountsController controller;

    private static OmniDriveTrayIcon trayIcon = null;
    private static FileSystem fileSystem = null;
    private static boolean shouldStartHidden = false;

    @Override
    public void start(Stage stage) throws Exception {
        trayIcon = new OmniDriveTrayIcon(stage, fileSystem);
        trayIcon.createTrayIcon(!shouldStartHidden);

        fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(SCREEN_FXML_PATH);
        fxmlLoader.setLocation(url);

        this.controller = fxmlLoader.getController();

        InputStream stream = fxmlLoader.getLocation().openStream();
        VBox rootPane = fxmlLoader.load(stream);

        Scene scene = new Scene(rootPane, 600, 400);
        stage.initStyle(StageStyle.DECORATED);

        stage.getIcons().add(new Image("/omnidrive_icon_1024.png"));
        stage.getIcons().add(new Image("/omnidrive_icon_rounded_128.png"));

        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("OmniDrive");

        if (shouldStartHidden) {
            stage.hide();
        } else {
            stage.show();
        }
    }

    public static void setTrayIconProgress(SyncProgress progress) {
        if (trayIcon != null) {
            trayIcon.setProgress(progress);
        }
    }

    public static void show(boolean startHidden, FileSystem fs) {
        shouldStartHidden = startHidden;
        fileSystem = fs;
        launch(null);
    }
}
