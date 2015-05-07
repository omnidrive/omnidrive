package omnidrive.api.google;

import com.google.api.services.drive.model.File;
import omnidrive.api.base.BaseFolder;

public class GoogleDriveFolder extends BaseFolder {

    private File file; // In the Drive API, a folder is essentially a file [https://developers.google.com/drive/web/folder]

    public GoogleDriveFolder(File folder, GoogleDriveUser owner) {
        super(owner);

        this.file = folder;
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public final String getName() {
        return this.file.getTitle();
    }

    public String getPath() {
        return null;
    }

    public final String getId() {
        return this.file.getId();
    }
}
