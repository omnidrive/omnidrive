package omnidrive.ui.login;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import omnidrive.api.base.*;
import omnidrive.api.managers.LoginManager;

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
    private Button boxButton;


    public LoginController() {

    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginManager.setLoginController(this);
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent evt) {
        try {
            if (evt.getSource() instanceof Button) {
                Button loginButton = (Button)evt.getSource();
                int typeIdx = Integer.parseInt(loginButton.getId());
                DriveType type = DriveType.values()[typeIdx];
                this.loginManager.login(type);
            }
        } catch (Exception ex) {
            this.loginManager.showError(ex.getMessage());
        }
    }

    public void showLoginWebView(final Authorizer auth, String authUrl) {
        final WebView browser = new WebView();
        final WebEngine engine = browser.getEngine();
        final LoginManager manager = this.loginManager;

        BorderPane authPane = new BorderPane();

        engine.load(authUrl);

        authPane.setCenter(browser);

        // listen to document load completed event
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.READY || newState == Worker.State.SUCCEEDED) {
                    try {
                        auth.fetchAuthCode(engine);
                    } catch (BaseException ex) {
                        manager.showError(ex.getMessage());
                    }
                } else if (newState == Worker.State.FAILED) {
                    manager.showError(engine.getLoadWorker().getException().getMessage());
                }
            }
        });

        // create scene
        this.loginStage.setTitle(auth.getName());
        Scene scene = new Scene(authPane, 750, 500);
        this.loginStage.setScene(scene);
        this.loginStage.showAndWait();
    }

    public void closeLoginWebView() {
        this.loginStage.close();
    }
}
