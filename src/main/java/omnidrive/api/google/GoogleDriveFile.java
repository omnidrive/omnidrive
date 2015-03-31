package omnidrive.api.google;

import com.google.api.services.drive.model.File;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseUser;

import java.util.Date;

public class GoogleDriveFile extends BaseFile {

    private final File file;

    public GoogleDriveFile(File file, GoogleDriveUser owner) {
        super(owner);

        this.file = file;
    }


    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public String getName() {
        return this.file.getTitle();
    }


    public String getPath() {
        return this.file.getTitle();
    }


    public long getSize() {
        return this.file.getFileSize();
    }


    public Date getLastModified() {
        return new Date(this.file.getModifiedDate().getValue());
    }


    public BaseUser getOwner() {
        return null;
    }

}
