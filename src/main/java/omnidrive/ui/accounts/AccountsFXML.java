package omnidrive.ui.accounts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import omnidrive.api.managers.AccountsManager;
import omnidrive.ui.managers.UIManager;

import java.net.URL;
import java.nio.file.Path;

public class AccountsFXML extends Application {

    private static final String SCREEN_FXML_PATH = "/AccountsScreen.fxml";

    private static Path omniDriveFolderPath;

    private static boolean shouldStartHidden = false;

    private static AccountsManager theAccountsManager;

    private static UIManager theUiManager;

    @Override
    public void start(Stage stage) throws Exception {
        URL url = getClass().getResource(SCREEN_FXML_PATH);
        FXMLLoader fxmlLoader = new FXMLLoader(url);

        // must be first - before get controller
        VBox rootPane = fxmlLoader.load();

        AccountsController controller = fxmlLoader.getController();
        controller.initialize(stage, theAccountsManager, omniDriveFolderPath, shouldStartHidden);
        if (theUiManager != null) {
            theUiManager.setController(controller);
        }

        Scene scene = new Scene(rootPane, 600, 517);
        stage.initStyle(StageStyle.DECORATED);

        //stage.getIcons().add(new Image(getClass().getResource("/omnidrive_circle_1000.png").getPath()));

        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("OmniDrive");

        if (shouldStartHidden) {
            stage.hide();
        } else {
            stage.show();
        }
    }

    public static void load(UIManager uiManager, AccountsManager accountsManager, boolean startHidden, Path folderPath) {
        theUiManager = uiManager;
        theAccountsManager = accountsManager;
        shouldStartHidden = startHidden;
        omniDriveFolderPath = folderPath;
        launch();
    }

}
