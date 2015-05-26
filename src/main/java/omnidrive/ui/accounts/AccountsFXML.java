package omnidrive.ui.accounts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;

public class AccountsFXML extends Application {

    private static final String SCREEN_FXML_PATH = "/AccountsScreen.fxml";

    private AccountsController controller;

    @Override
    public void start(Stage stage) throws Exception {
        MenuBar menuBar = new MenuBar ();
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac"))
            menuBar.useSystemMenuBarProperty().set(true);

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(SCREEN_FXML_PATH);
        fxmlLoader.setLocation(url);

        this.controller = fxmlLoader.getController();

        InputStream stream = fxmlLoader.getLocation().openStream();
        VBox rootPane = (VBox)fxmlLoader.load(stream);

        Scene scene = new Scene(rootPane, 600, 400);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("OmniDrive");
        stage.getIcons().add(new Image("/omnidrive_128.png"));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
