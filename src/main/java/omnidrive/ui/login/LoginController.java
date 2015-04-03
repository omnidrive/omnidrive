package omnidrive.ui.login;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import omnidrive.api.base.*;
import omnidrive.api.managers.LoginManager;


import java.net.URL;
import java.util.ResourceBundle;

import omnidrive.ui.general.PopUpView;

public class LoginController implements Initializable {

    private final Stage loginStage = new Stage();

    private final LoginManager loginManager = LoginManager.getLoginManager();

    private final BorderPane borderPane = new BorderPane();

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

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginManager.setLoginController(this);
    }

    @FXML
    protected void onDropboxButtonClick() {
        try {
            this.loginManager.dropboxLogin();
        } catch (BaseException ex) {
            this.loginManager.showError(ex.getMessage());
        }
    }

    @FXML
    protected void onGoogleDriveButtonClick() {
        try {
            this.loginManager.googleDriveLogin();
        } catch (BaseException ex) {
            this.loginManager.showError(ex.getMessage());
        }
    }

    @FXML
    protected void onOneDriveButtonClick() {
        try {
            this.loginManager.oneDriveLogin();
        } catch (BaseException ex) {
            this.loginManager.showError(ex.getMessage());
        }
    }

    public void showLoginWebView(final BaseApi api, String authUrl) {
        final WebView browser = new WebView();
        final WebEngine engine = browser.getEngine();

        engine.load(authUrl);
        this.borderPane.setCenter(browser);

        final LoginManager manager = this.loginManager;

        // listen to document load completed event
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                String url = engine.getLocation();
                if (newState == Worker.State.READY || newState == Worker.State.SUCCEEDED) {
                    try {
                        api.fetchAuthCode(engine);
                    } catch (BaseException ex) {
                        manager.showError(ex.getMessage());
                    }
                } else if (newState == Worker.State.FAILED) {
                    manager.showError(engine.getLoadWorker().getException().getMessage());
                }
            }
        });

        // create scene
        this.loginStage.setTitle(api.getName());
        Scene scene = new Scene(this.borderPane, 750, 500);
        this.loginStage.setScene(scene);
        this.loginStage.show();
    }

    public void closeLoginWebView() {
        this.loginStage.close();
    }
}
