package omnidrive.ui.accounts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import omnidrive.api.managers.AccountsManager;
import omnidrive.ui.general.OmniDriveTrayIcon;
import omnidrive.ui.general.SyncProgress;

import java.net.URL;
import java.nio.file.Path;

public class AccountsFXML extends Application {

    private static final String SCREEN_FXML_PATH = "/AccountsScreen.fxml";

    public FXMLLoader fxmlLoader;

    private static OmniDriveTrayIcon trayIcon;

    private static Stage theStage;

    private static Path omniDriveFolderPath;

    private static boolean shouldStartHidden = false;

    private static AccountsManager accountsManager;

    @Override
    public void start(Stage stage) throws Exception {
        theStage = stage;

        URL url = getClass().getResource(SCREEN_FXML_PATH);
        this.fxmlLoader = new FXMLLoader(url);

        // must be first - before get controller
        VBox rootPane = this.fxmlLoader.load();

        trayIcon = new OmniDriveTrayIcon(stage, omniDriveFolderPath);
        trayIcon.createTrayIcon(!shouldStartHidden);

        AccountsController controller = this.fxmlLoader.getController();
        controller.setAccountsManager(accountsManager);
        controller.startSizeUpdater();

        Scene scene = new Scene(rootPane, 600, 450);
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

    public static void setSyncProgress(SyncProgress progress) {
        if (trayIcon != null) {
            trayIcon.setProgress(progress);
        }
    }

    public static void show() {
        if (theStage != null) {
            theStage.show();
        }
    }

    public static void hide() {
        if (theStage != null) {
            theStage.hide();
        }
    }

    public static void load(AccountsManager manager, boolean startHidden, Path folderPath) {
        accountsManager = manager;
        shouldStartHidden = startHidden;
        omniDriveFolderPath = folderPath;
        launch();
    }

}
