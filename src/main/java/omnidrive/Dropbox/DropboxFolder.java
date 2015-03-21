package omnidrive.Dropbox;

import com.dropbox.core.DbxEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by assafey on 3/21/15.
 */
public class DropboxFolder {

    private DropboxUser owner;
    private DbxEntry.Folder folder;
    private List<DropboxFile> files;
    private long size;

    public DropboxFolder(DbxEntry.Folder folder, DropboxUser owner) {
        this.owner = owner;
        this.folder = folder;
        this.files = getFiles();
        this.size = calculateSize();
    }

    private List<DropboxFile> getFiles() {
        return new ArrayList<DropboxFile>(); // TODO - get folder's files
    }

    private long calculateSize() {
        return 0; // TODO - calculate size
    }

    public DropboxUser getOwner() {
        return this.owner;
    }

    public String getPath() {
        return this.folder.path;
    }

    public String getName() {
        return this.folder.name;
    }

    public long getSize() {
        return this.size;
    }
}
