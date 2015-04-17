package omnidrive.api.google;

import com.google.api.services.drive.model.ParentReference;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseFolder;

import java.util.List;

public class GoogleDriveFolder extends BaseFolder {

    private ParentReference parent;

    public GoogleDriveFolder(ParentReference parent, GoogleDriveUser owner) {
        super(owner);

        this.parent = parent;
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public final String getName() {
        return parent.getId();
    }

    public String getPath() {
        return null;
    }

    public final String getId() {
        return parent.getId();
    }


    public final List<BaseFolder> getFolders() {
        return null;
    }


    public final List<BaseFile> getFiles() {
        return null;
    }
}
