package omnidrive.api.google;

import com.google.api.services.drive.model.ParentReference;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseFolder;
import omnidrive.api.base.BaseUser;

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

    public String getName() {
        return parent.getId();
    }


    public String getPath() {
        return parent.getId();
    }


    public List<BaseFolder> getFolders() {
        return null;
    }


    public List<BaseFile> getFiles() {
        return null;
    }
}
