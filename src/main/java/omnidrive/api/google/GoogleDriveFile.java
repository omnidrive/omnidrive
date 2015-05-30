package omnidrive.api.google;

import com.google.api.services.drive.model.File;
import omnidrive.api.base.BaseFile;

import java.util.Date;

public class GoogleDriveFile extends BaseFile {

    private final File file;

    public GoogleDriveFile(File file, GoogleDriveAccount owner) {
        super(owner);

        this.file = file;
    }


    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public final String getName() {
        return this.file.getTitle();
    }


    public final String getPath() {
        return this.file.getTitle();
    }

    public final String getId() {
        return this.file.getId();
    }

    public final long getSize() {
        return this.file.getFileSize();
    }


    public final Date getLastModified() {
        return new Date(this.file.getModifiedDate().getValue());
    }

}
