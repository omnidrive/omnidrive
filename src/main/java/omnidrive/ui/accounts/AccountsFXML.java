package omnidrive.ui.accounts;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import omnidrive.OmniDrive;
import omnidrive.ui.nsmenufx.NSMenuBarAdapter;
import omnidrive.ui.nsmenufx.convert.ToJavaFXConverter;

import java.io.InputStream;
import java.net.URL;

public class AccountsFXML extends Application {

    public static FXMLLoader fxmlLoader;
    private static final String SCREEN_FXML_PATH = "/AccountsScreen.fxml";

    private AccountsController controller;

    @Override
    public void start(Stage stage) throws Exception {
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
        stage.show();
    }

    public static void show() {
        launch(null);
    }
}
