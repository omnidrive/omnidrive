package omnidrive.ui.login;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import omnidrive.api.base.*;
import omnidrive.api.managers.LoginManager;

import omnidrive.ui.general.PopupView;


import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final Stage loginStage = new Stage();
    private final LoginManager loginManager = LoginManager.getLoginManager();

    @FXML
    private GridPane loginPane;

    @FXML
    private Label omniTitleLabel;

    @FXML
    private Button dropboxButton;

    @FXML
    private Button googleDriveButton;

    @FXML
    private Button oneDriveButton;


    public LoginController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginManager.setLoginController(this);
    }

    @FXML
    protected void onDropboxButtonClick() {
        try {
            this.loginManager.dropboxLogin();
        } catch (BaseException ex) {
            PopupView.showError(new Point2D(this.loginPane.getLayoutX(), this.loginPane.getLayoutY()), ex.getMessage());
        }
    }

    @FXML
    protected void onGoogleDriveButtonClick() {

    }

    @FXML
    protected void onOneDriveButtonClick() {

    }

    public void showLoginWebView(final BaseApi api, String authUrl) {
        BorderPane borderPane = new BorderPane();

        final WebView browser = new WebView();
        final WebEngine engine = browser.getEngine();

        engine.load(authUrl);
        borderPane.setCenter(browser);

        // listen to document load completed event
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    api.authCompleted(engine);
                }
            }
        });

        // create scene
        this.loginStage.setTitle(api.getName());
        Scene scene = new Scene(borderPane, 750, 500);
        this.loginStage.setScene(scene);
        this.loginStage.show();
    }

    public void closeLoginWebView() {
        this.loginStage.close();
    }
}
