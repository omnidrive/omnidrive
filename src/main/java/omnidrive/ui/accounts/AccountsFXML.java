package omnidrive.ui.accounts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;
import java.net.URL;

public class AccountsFXML extends Application {

    private static final String SCREEN_FXML_PATH = "/AccountsScreen.fxml";

    private AccountsController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(SCREEN_FXML_PATH);
        fxmlLoader.setLocation(url);

        this.controller = fxmlLoader.getController();

        InputStream stream = fxmlLoader.getLocation().openStream();
        VBox rootPane = (VBox)fxmlLoader.load(stream);

        Scene scene = new Scene(rootPane, 600, 400);
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("OmniDrive");
        stage.getIcons().add(new Image("/omnidrive_icon_rounded_128.png"));
        stage.show();
    }

    public static void show() {
        launch(null);
    }
}
