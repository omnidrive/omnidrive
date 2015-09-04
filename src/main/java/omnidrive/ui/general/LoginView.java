package omnidrive.ui.general;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import omnidrive.api.account.Account;
import omnidrive.api.auth.AuthListener;
import omnidrive.api.account.AccountAuthorizer;
import omnidrive.api.account.AccountException;
import omnidrive.api.account.AccountType;

public class LoginView {

    private final Stage loginStage;

    public LoginView() {
        this.loginStage = new Stage();
    }

    public void show(final AuthListener authListener, final AccountAuthorizer authorizer,
                     final AccountType type, final String authUrl) {

        final WebView browser = new WebView();
        final WebEngine engine = browser.getEngine();
        final LoggedInMarker marker = new LoggedInMarker();

        BorderPane authPane = new BorderPane();

        engine.load(authUrl);

        authPane.setCenter(browser);

        // listen to document load completed event
        if (type != AccountType.GoogleDrive) {
            engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
                @Override
                public void handle(WebEvent<String> event) {
                    if (!marker.isLoggedIn()) {
                        try {
                            Account newAccount = authorizer.authenticate(engine);
                            if (newAccount != null) {
                                loginStage.hide();
                                authorizer.finishAuthentication(newAccount);
                                marker.markLoggedIn();
                            }
                        } catch (AccountException ex) {
                            authListener.authFailure(type, ex.getMessage());
                        }
                    }
                }
            });
        } else {
            engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                    if (newState != Worker.State.FAILED && newState != Worker.State.CANCELLED) {
                        try {
                            Account newAccount = authorizer.authenticate(engine);
                            if (newAccount != null) {
                                loginStage.hide();
                                authorizer.finishAuthentication(newAccount);
                            }
                        } catch (AccountException ex) {
                            authListener.authFailure(type, ex.getMessage());
                        }
                    } else {
                        authListener.authFailure(type, engine.getLoadWorker().getException().getMessage());
                    }
                }
            });
        }


        // create scene
        this.loginStage.setTitle(type.toString());
        Scene scene = new Scene(authPane, 750, 650);
        this.loginStage.setScene(scene);
        this.loginStage.showAndWait();
    }

    public void close() {
        this.loginStage.close();
    }

    private class LoggedInMarker {
        private boolean loggedIn;

        public LoggedInMarker() {
            loggedIn = false;
        }

        public void markLoggedIn() {
            loggedIn = true;
        }

        public boolean isLoggedIn() {
            return loggedIn;
        }
    }
}