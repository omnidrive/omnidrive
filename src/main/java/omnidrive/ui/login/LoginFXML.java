package omnidrive.ui.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;


public class LoginFXML extends Application {

    private static final String LOGIN_SCREEN_XML_PATH = "/LoginScreen.fxml";

    private LoginController loginController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(LOGIN_SCREEN_XML_PATH);
        fxmlLoader.setLocation(url);

        this.loginController = fxmlLoader.getController();

        InputStream stream = fxmlLoader.getLocation().openStream();
        GridPane root = (GridPane)fxmlLoader.load(stream);
        Scene scene = new Scene(root, 400, 600);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
