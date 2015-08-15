package omnidrive.ui.general;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class PopupView {

    private final Stage stage;
    private final ImageView iconImageView;
    private final Label messageLabel;
    private final Button closeButton;
    private final Pane pane;

    private static PopupView popupView;

    private PopupView() {
        this.iconImageView = new ImageView(new Image("/omnidrive_icon_rounded_128.png"));
        this.iconImageView.setFitWidth(80);
        this.iconImageView.setFitHeight(80);
        this.iconImageView.setLayoutX(20);
        this.iconImageView.setLayoutY(20);

        this.messageLabel = new Label();
        this.messageLabel.setFont(new Font(16));
        this.messageLabel.setPrefWidth(360);
        this.messageLabel.setPrefHeight(200);
        this.messageLabel.setLayoutX(20);
        this.messageLabel.setLayoutY(120);
        this.messageLabel.setAlignment(Pos.TOP_LEFT);
        this.messageLabel.setTextAlignment(TextAlignment.LEFT);
        this.messageLabel.setWrapText(true); // multiline

        final PopupView self = this;

        this.closeButton = new Button();
        this.closeButton.setLayoutX(300);
        this.closeButton.setLayoutY(260);
        this.closeButton.setPrefWidth(80);
        this.closeButton.setPrefHeight(25);
        this.closeButton.setText("Close");
        this.closeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                self.close();
            }
        });

        this.pane = new Pane();
        this.pane.getChildren().addAll(this.messageLabel, this.iconImageView, this.closeButton);

        this.stage = new Stage();
        this.stage.setTitle("OmniDrive");
    }

    public static PopupView popup() {
        if (popupView == null) {
            popupView = new PopupView();
        }

        return popupView;
    }

    public void info(String message) {
        this.messageLabel.setText(message);

        Scene scene = new Scene(this.pane, 400, 300);

        this.stage.setScene(scene);
        this.stage.show();
    }

    public void error(String message) {
        this.messageLabel.setText(message);

        Scene scene = new Scene(this.pane, 400, 300);

        this.stage.setScene(scene);
        this.stage.show();
    }

    private void close() {
        final Stage stage = this.stage;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.close();
            }
        });
    }
}
