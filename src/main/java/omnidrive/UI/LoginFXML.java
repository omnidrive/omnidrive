package omnidrive.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;


public class LoginFXML extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/omnidrive/UI/LoginScreen.fxml");
        fxmlLoader.setLocation(url);

        InputStream stream = fxmlLoader.getLocation().openStream();
        GridPane root = (GridPane)fxmlLoader.load(stream);
        Scene scene = new Scene(root, 450, 650);
        stage.setResizable(false);
        stage.setScene(scene);
        LoginController controller = fxmlLoader.getController();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
