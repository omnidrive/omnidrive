package omnidrive.api.google;

import com.google.api.services.drive.model.ParentReference;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseFolder;

import java.util.List;

public class GoogleDriveFolder extends BaseFolder {

    private String folderId;

    public GoogleDriveFolder(String folderId, GoogleDriveUser owner) {
        super(owner);

        this.folderId = folderId;
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public final String getName() {
        return null; // TODO - how the hell to get folder name??
    }

    public String getPath() {
        return null;
    }

    public final String getId() {
        return folderId;
    }
}
