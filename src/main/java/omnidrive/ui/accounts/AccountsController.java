package omnidrive.ui.accounts;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import omnidrive.api.account.AccountAuthorizer;
import omnidrive.api.account.Account;
import omnidrive.api.account.AccountType;
import omnidrive.api.account.AccountException;
import omnidrive.api.managers.AccountsManager;
import omnidrive.api.managers.LoginManager;
import omnidrive.api.auth.AuthService;
import omnidrive.ui.general.*;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class AccountsController implements Initializable, AuthService, Runnable {

    private static final int DOUBLE_CLICK = 2;

    private static final int SIZE_UPDATER_SLEEP_TIME = 10000; //msec

    private static final int NUM_OF_ACCOUNTS = AccountType.length();

    private static final String BigIconImagePaths[] =
            {"/dropbox_icon.png", "/google_drive_icon.png", "/box_icon.png", "/onedrive_icon.png"};
    private static final String SmallIconImagePaths[] =
            {"/dropbox_icon_small.png", "/google_drive_icon_small.png", "/box_icon_small.png", "/onedrive_icon_small.png"};

    private static final int HeightOfUnregisteredCell = 80;
    private static final int HeightOfRegisteredCell = 40;

    private final LoginManager loginManager;

    private AccountsManager accountsManager;

    private final LoginView loginView;

    private final Thread sizeUpdater;

    private OmniDriveTrayIcon omniDriveTrayIcon;

    private Stage parentStage;

    @FXML
    private Button addAccountButton;

    @FXML
    private Button removeAccountButton;

    @FXML
    private ListView<Pane> registeredAccountsListView;

    @FXML
    private ListView<Pane> unregisteredAccountsListView;

    @FXML
    private Label totalSizeLabel;

    @FXML
    private Label freeSizeLabel;


    public AccountsController() {
        this.loginManager = new LoginManager();
        this.loginView = new LoginView();
        this.sizeUpdater = new Thread(this, "tSizeUpdater");
    }

    private void startSizeUpdater() {
        if (this.accountsManager != null) {
            this.sizeUpdater.start();
        }
    }

    public void initialize(Stage stage, AccountsManager accountsManager, Path omniDriveFolderPath, boolean startHidden) {
        this.parentStage = stage;
        this.accountsManager = accountsManager;
        createTrayIcon(stage, omniDriveFolderPath, startHidden);
        restoreAccounts();

        startSizeUpdater();
    }

    public void showStage() {
        final Stage stage = this.parentStage;

        if (stage == null) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.show();
            }
        });
    }

    public void hideStage() {
        final Stage stage = this.parentStage;

        if (stage == null) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.hide();
            }
        });
    }

    public void setSyncProgress(final SyncProgress progress) {
        final OmniDriveTrayIcon trayIcon = this.omniDriveTrayIcon;

        if (trayIcon == null) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                trayIcon.setProgress(progress);
            }
        });
    }

    private void createTrayIcon(Stage stage, Path omniDriveFolderPath, boolean shouldStartHidden) {
        omniDriveTrayIcon = new OmniDriveTrayIcon(stage, omniDriveFolderPath);
        omniDriveTrayIcon.setAccountsManager(this.accountsManager);
        try {
            omniDriveTrayIcon.applyStyle(!shouldStartHidden);
        } catch (Exception ex) {
            System.out.println("Failed to apply style to omnidrive tray icon.");
        }
    }

    private void restoreAccounts() {
        final AccountsManager manager = this.accountsManager;

        if (manager == null) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<Account> accounts = manager.getActiveAccounts();
                for (Account account : accounts) {
                    addAccountToListView(account.getType());
                }
                fetchCloudTotalSize();
                fetchCloudFreeSpace();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addAccountsToUnregisteredListView();
        disableControlsFocus();
    }

    @Override
    public void attemptToAuth(AccountType type, AccountAuthorizer authorizer, String authUrl) {
        this.loginView.show(this.loginManager, authorizer, type, authUrl);
    }

    @Override
    public void reportAuthError(AccountType type, String message) {
        PopupView.popup().info(message);
    }

    @Override
    public void accountAuthorized(Account account) {
        this.accountsManager.addNewAccount(account);
        addAccountToListView(account.getType());
        this.loginView.close();
        fetchCloudTotalSize();
        fetchCloudFreeSpace();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(SIZE_UPDATER_SLEEP_TIME);
            } catch (InterruptedException ex) {
                System.out.println("Failed to sleep between fetch cloud size");
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    fetchCloudFreeSpace();
                }
            });
        }
    }

    private void fetchCloudFreeSpace() {
        try {
            Float freeSpace = new Float((double)this.accountsManager.getCloudFreeSize() / (1024.0 * 1024.0 * 1024.0));
            this.freeSizeLabel.setText("Cloud Free Space: " + String.format("%.03f GB", freeSpace));
        } catch (AccountException ex) {
            System.out.println("Failed to fetch cloud free space");
        }
    }

    private void fetchCloudTotalSize() {
        try {
            Float totalSize = new Float((double)this.accountsManager.getCloudTotalSize() / (1024.0 * 1024.0 * 1024.0));
            this.totalSizeLabel.setText("Cloud Total Size: " + String.format("%.03f GB", totalSize));
        } catch (AccountException ex) {
            System.out.println("Failed to fetch cloud total size");
        }
    }

    @FXML
    protected void onAddAccountButtonClicked() {
        accountClicked();
    }

    @FXML
    protected void onRemoveAccountButtonClicked() {
        int selectedIndex = this.registeredAccountsListView.getFocusModel().getFocusedIndex();

        if (selectedIndex >= 0) {
            LogoListCell selectedCell = (LogoListCell)this.registeredAccountsListView.getItems().get(selectedIndex);
            AccountType type = selectedCell.getType();
            this.loginManager.remove(type);
            this.accountsManager.removeAccount(type);
            removeAccountFromListView(selectedIndex);
        } else {
            PopupView.popup().info("Please select a registered cloud.");
        }
    }

    private void accountClicked() {
        int selectedIndex = this.unregisteredAccountsListView.getFocusModel().getFocusedIndex();

        if (selectedIndex >= 0) {
            LogoListCell selectedCell = (LogoListCell)this.unregisteredAccountsListView.getItems().get(selectedIndex);
            AccountType type = selectedCell.getType();
            if (!this.accountsManager.isRegistered(type)) {
                this.loginManager.login(type, this);
            }
        } else {
            PopupView.popup().info("Please select an unregistered cloud.");
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
            AccountType type = AccountType.getType(cellIdx);
            Image iconImage = new Image(BigIconImagePaths[cellIdx]);
            cells[cellIdx] = new LogoListCell(type, iconImage);
            cells[cellIdx].setSize(this.unregisteredAccountsListView.getPrefWidth() - 20, HeightOfUnregisteredCell);
            cells[cellIdx].setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == DOUBLE_CLICK) {
                        accountClicked();
                    }
                }
            });
        }

        return cells;
    }

    private LogoListCell createRegisteredCloudCell(AccountType type) {
        final int SmallGap = 5;

        Image iconImage = new Image(SmallIconImagePaths[type.ordinal()]);

        LogoListCell cell = new LogoListCell(type, iconImage, 16, SmallGap, SmallGap);
        cell.setSize(this.registeredAccountsListView.getPrefWidth() - 20, HeightOfRegisteredCell);

        return cell;
    }

    private void addAccountsToUnregisteredListView() {
        LogoListCell cells[] = createUnregisteredCloudCells();
        this.unregisteredAccountsListView.getItems().addAll(cells);
        this.unregisteredAccountsListView.layout();
    }

    private void addAccountToListView(AccountType type) {
        LogoListCell logoListCell = createRegisteredCloudCell(type);
        this.registeredAccountsListView.getItems().add(logoListCell);
        this.registeredAccountsListView.layout();
    }

    private void removeAccountFromListView(int index) {
        this.registeredAccountsListView.getItems().remove(index);
        this.registeredAccountsListView.layout();
    }
}
