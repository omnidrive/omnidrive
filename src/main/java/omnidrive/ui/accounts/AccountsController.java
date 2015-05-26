package omnidrive.ui.accounts;

import com.sun.javafx.collections.ImmutableObservableList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import omnidrive.api.base.DriveType;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {

    private static final int NUM_OF_ACCOUNTS = DriveType.values().length;

    @FXML
    private final ListView accountsListView = new ListView();

    @FXML
    private final ListView cloudsListView = new ListView();



    public void initialize(URL url, ResourceBundle resourceBundle) {
        String clouds[] = {"Dropbox", "Google Drive", "Box"};
        Label cloudLabels[] = new Label[NUM_OF_ACCOUNTS];
        for (int i = 0; i < NUM_OF_ACCOUNTS; i++) {
            cloudLabels[i] = new Label(clouds[i]);
        }

        ObservableList cloudsList = new ImmutableObservableList(cloudLabels);
        this.cloudsListView.setItems(cloudsList);
    }

    @FXML
    protected void onAddAccountButtonClicked() {

    }

    @FXML
    protected void onRemoveAccountButtonClicked() {

    }

}
