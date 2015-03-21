package omnidrive.Dropbox;

import com.dropbox.core.DbxEntry;

import java.util.Date;

/**
 * Created by assafey on 3/21/15.
 */
public class DropboxFile {

    private DropboxUser owner;
    private DbxEntry.File file;

    public DropboxFile(DbxEntry.File file, DropboxUser owner) {
        this.owner = owner;
        this.file = file;
    }

    public DropboxUser getOwner() {
        return this.owner;
    }

    public String getPath() {
        return this.file.path;
    }

    public String getName() {
        return this.file.name;
    }

    public Date getLastModified() {
        return this.file.lastModified;
    }

    public long getSize() {
        return this.file.numBytes;
    }
}
