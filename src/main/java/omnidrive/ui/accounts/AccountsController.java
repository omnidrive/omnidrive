package omnidrive.ui.accounts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.api.managers.LoginManager;
import omnidrive.api.auth.AuthService;
import omnidrive.ui.general.LoginView;
import omnidrive.ui.general.PopupView;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable, AuthService {

    private static final int NUM_OF_ACCOUNTS = DriveType.values().length;
    private static final int CLOUD_ICON_SIZE = 80;
    private static final int CLOUD_LOGO_WIDTH = 220;
    private static final int CLOUD_LOGO_HEIGHT = 80;
    private static final int CLOUD_PANE_WIDTH = 300;
    private static final int CLOUD_PANE_HEIGHT = 80;

    private final String cloudIconPaths[] = {"/dropbox_icon.png", "/google_drive_icon.png", "/box_icon.png"};
    private final String cloudLogoPaths[] = {"/dropbox_logo.png", "/google_drive_logo.png", "/box_logo.png"};

    private final Pane cloudPanes[] = new Pane[NUM_OF_ACCOUNTS];

    private final LoginManager loginManager;

    private final AccountsManager accountsManager;

    private final LoginView loginView;

    @FXML
    private Button addAccountButton;

    @FXML
    private Button removeAccountButton;

    @FXML
    private ListView<Pane> accountsListView;

    @FXML
    private ListView<Pane> cloudsListView;


    public AccountsController() {
        this.loginManager = LoginManager.getLoginManager();
        this.accountsManager = AccountsManager.getAccountsManager();
        this.loginView = new LoginView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addAllCloudsToListView();
        disableControlsFocus();
    }

    @Override
    public void connect(DriveType type, BaseApi api, String authUrl) {
        this.loginView.show(this.loginManager, api, type, authUrl);
    }

    @Override
    public void report(DriveType type, String message) {
        // TODO - popup message
        PopupView popUp = new PopupView();
        popUp.info(message);
    }

    @Override
    public void terminate(DriveType type, BaseAccount account) {
        this.accountsManager.setAccount(type, account);
        addAccountToListView(type);
        this.loginView.close();
    }

    @FXML
    protected void onAddAccountButtonClicked() {
        int selectedIndex = this.cloudsListView.getFocusModel().getFocusedIndex();
        if (selectedIndex >= 0) {
            DriveType type = DriveType.values()[selectedIndex];
            this.loginManager.login(type, this);
        }
    }

    @FXML
    protected void onRemoveAccountButtonClicked() {
        int selectedIndex = this.cloudsListView.getFocusModel().getFocusedIndex();
        if (selectedIndex >= 0) {
            DriveType type = DriveType.values()[selectedIndex];
            this.loginManager.remove(type);
            this.accountsManager.removeAccount(type);
        }
    }

    private void disableControlsFocus() {
        this.addAccountButton.setFocusTraversable(false);
        this.removeAccountButton.setFocusTraversable(false);
        this.accountsListView.setFocusTraversable(false);
        this.cloudsListView.setFocusTraversable(false);
    }

    private void addAllCloudsToListView() {
        ImageView cloudIconImageViews[] = new ImageView[NUM_OF_ACCOUNTS];
        ImageView cloudLogoImageViews[] = new ImageView[NUM_OF_ACCOUNTS];

        for (int i = 0; i < NUM_OF_ACCOUNTS; i++) {
            cloudIconImageViews[i] = new ImageView(new Image(this.cloudIconPaths[i]));
            cloudIconImageViews[i].setLayoutX(0);
            cloudIconImageViews[i].setLayoutY(0);
            cloudIconImageViews[i].setFitHeight(CLOUD_ICON_SIZE);
            cloudIconImageViews[i].setFitWidth(CLOUD_ICON_SIZE);

            cloudLogoImageViews[i] = new ImageView(new Image(this.cloudLogoPaths[i]));
            cloudLogoImageViews[i].setLayoutX(CLOUD_ICON_SIZE);
            cloudLogoImageViews[i].setLayoutY(0);
            cloudLogoImageViews[i].setFitHeight(CLOUD_LOGO_HEIGHT);
            cloudLogoImageViews[i].setFitWidth(CLOUD_LOGO_WIDTH);

            this.cloudPanes[i] = new Pane();
            this.cloudPanes[i].setPrefHeight(CLOUD_PANE_HEIGHT);
            this.cloudPanes[i].setPrefWidth(CLOUD_PANE_WIDTH);

            this.cloudPanes[i].getChildren().add(cloudIconImageViews[i]);
            this.cloudPanes[i].getChildren().add(cloudLogoImageViews[i]);
        }

        ObservableList cloudsObservableList = FXCollections.observableArrayList();
        cloudsObservableList.addAll(this.cloudPanes);

        this.cloudsListView.setItems(cloudsObservableList);
    }

    private void addAccountToListView(DriveType type) {
        // FIXME - add the account to UI list
        this.accountsListView.getItems().add(this.cloudPanes[type.ordinal()]);
    }
}
