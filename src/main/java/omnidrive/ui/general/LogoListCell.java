package omnidrive.ui.general;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class LogoListCell extends Pane {

    private static int MARGIN_GAP = 10;
    private static int UI_ELEMENTS_GAP = 10;
    private static final int DEFAULT_TEXT_SIZE = 32;

    private final ImageView iconImageView;
    private final Label logoTextLabel;

    public LogoListCell(Image iconImage, String text, int textSize, int margin, int gap) {
        this(iconImage, text, textSize);
        MARGIN_GAP = margin;
        UI_ELEMENTS_GAP = gap;
    }

    public LogoListCell(Image iconImage, String text) {
        this(iconImage, text, DEFAULT_TEXT_SIZE);
    }

    public LogoListCell(Image iconImage, String text, int textSize) {
        this.iconImageView = new ImageView(iconImage);
        this.logoTextLabel = new Label(text);
        this.logoTextLabel.setFont(new Font(textSize));
        this.getChildren().addAll(this.iconImageView, this.logoTextLabel);
    }

    public void setSize(double width, double height) {
        setPrefWidth(width);
        setPrefHeight(height);
        updateContent(width, height);
    }

    private void updateContent(double width, double height) {
        double iconImageSize = height < width ? height : width;
        iconImageSize -= (MARGIN_GAP * 2);

        this.iconImageView.setFitHeight(iconImageSize);
        this.iconImageView.setFitWidth(iconImageSize);
        this.iconImageView.setX(MARGIN_GAP);
        this.iconImageView.setY(MARGIN_GAP);

        double logoWidth = width - iconImageSize - (MARGIN_GAP * 2) - UI_ELEMENTS_GAP;
        double logoHeight = height - (MARGIN_GAP * 2);
        this.logoTextLabel.setPrefWidth(logoWidth);
        this.logoTextLabel.setPrefHeight(logoHeight);
        this.logoTextLabel.setLayoutX(MARGIN_GAP + iconImageSize + UI_ELEMENTS_GAP);
        this.logoTextLabel.setLayoutY(MARGIN_GAP);
    }
}
