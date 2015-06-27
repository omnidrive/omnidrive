package omnidrive.ui.accounts;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import omnidrive.api.base.BaseApi;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.DriveType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.api.managers.LoginManager;
import omnidrive.api.auth.AuthService;
import omnidrive.ui.general.LoginView;
import omnidrive.ui.general.LogoListCell;
import omnidrive.ui.general.PopupView;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable, AuthService {

    private static final int NUM_OF_ACCOUNTS = DriveType.length();

    private static final String BigIconImagePaths[] =
            {"/dropbox_icon.png", "/google_drive_icon.png", "/box_icon.png"};
    private static final String SmallIconImagePaths[] =
            {"/dropbox_icon_small.png", "/google_drive_icon_small.png", "/box_icon_small.png"};

    private static final int HeightOfUnregisteredCell = 80;
    private static final int HeightOfRegisteredCell = 40;

    private final LoginManager loginManager;

    private final AccountsManager accountsManager;

    private final LoginView loginView;

    @FXML
    private Button addAccountButton;

    @FXML
    private Button removeAccountButton;

    @FXML
    private ListView<Pane> registeredAccountsListView;

    @FXML
    private ListView<Pane> unregisteredAccountsListView;


    public AccountsController() {
        this.loginManager = LoginManager.getLoginManager();
        this.accountsManager = new AccountsManager();
        this.loginView = new LoginView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addAccountsToUnregisteredListView();
        disableControlsFocus();
    }

    @Override
    public void attempt(DriveType type, BaseApi api, String authUrl) {
        this.loginView.show(this.loginManager, api, type, authUrl);
    }

    @Override
    public void report(DriveType type, String message) {
        // TODO - popup message
        PopupView.popup().info(message);
    }

    @Override
    public void succeed(DriveType type, BaseAccount account) {
        this.accountsManager.setAccount(type, account);
        addAccountToListView(type);
        this.loginView.close();

        // TODO - save token to manifest
    }

    @FXML
    protected void onAddAccountButtonClicked() {
        int selectedIndex = this.unregisteredAccountsListView.getFocusModel().getFocusedIndex();

        if (selectedIndex >= 0) {
            LogoListCell selectedCell = (LogoListCell)this.unregisteredAccountsListView.getItems().get(selectedIndex);
            DriveType type = selectedCell.getType();
            if (!this.accountsManager.isRegistered(type)) {
                this.loginManager.login(type, this);
            }
        } else {
            PopupView.popup().info("Please select an unregistered cloud.");
        }
    }

    @FXML
    protected void onRemoveAccountButtonClicked() {
        int selectedIndex = this.registeredAccountsListView.getFocusModel().getFocusedIndex();

        if (selectedIndex >= 0) {
            LogoListCell selectedCell = (LogoListCell)this.registeredAccountsListView.getItems().get(selectedIndex);
            DriveType type = selectedCell.getType();
            this.loginManager.remove(type);
            this.accountsManager.removeAccount(type);
            removeAccountFromListView(selectedIndex);
        } else {
            PopupView.popup().info("Please select a registered cloud.");
        }
    }

    private void disableControlsFocus() {
        this.addAccountButton.setFocusTraversable(false);
        this.removeAccountButton.setFocusTraversable(false);
        this.unregisteredAccountsListView.setFocusTraversable(false);
        this.registeredAccountsListView.setFocusTraversable(false);
    }

    private LogoListCell[] createUnregisteredCloudCells() {
        LogoListCell cells[] = new LogoListCell[NUM_OF_ACCOUNTS];

        for (int cellIdx = 0; cellIdx < NUM_OF_ACCOUNTS; cellIdx++) {
            DriveType type = DriveType.getType(cellIdx);
            Image iconImage = new Image(BigIconImagePaths[cellIdx]);
            cells[cellIdx] = new LogoListCell(type, iconImage);
            cells[cellIdx].setSize(this.unregisteredAccountsListView.getPrefWidth() - 10, HeightOfUnregisteredCell);
        }

        return cells;
    }

    private LogoListCell createRegisteredCloudCell(DriveType type) {
        final int SmallGap = 5;

        Image iconImage = new Image(SmallIconImagePaths[type.ordinal()]);

        LogoListCell cell = new LogoListCell(type, iconImage, 16, SmallGap, SmallGap);
        cell.setSize(this.registeredAccountsListView.getPrefWidth() - 10, HeightOfRegisteredCell);

        return cell;
    }

    private void addAccountsToUnregisteredListView() {
        LogoListCell cells[] = createUnregisteredCloudCells();
        this.unregisteredAccountsListView.getItems().addAll(cells);
        this.unregisteredAccountsListView.layout();
    }

    private void addAccountToListView(DriveType type) {
        LogoListCell logoListCell = createRegisteredCloudCell(type);
        this.registeredAccountsListView.getItems().add(logoListCell);
        this.registeredAccountsListView.layout();
    }

    private void removeAccountFromListView(int index) {
        this.registeredAccountsListView.getItems().remove(index);
        this.registeredAccountsListView.layout();
    }
}
