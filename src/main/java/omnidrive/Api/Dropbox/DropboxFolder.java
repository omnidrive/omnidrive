package omnidrive.api.dropbox;

import com.dropbox.core.DbxEntry;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseFolder;
import omnidrive.api.base.BaseUser;

import java.util.ArrayList;
import java.util.List;


public class DropboxFolder implements BaseFolder {

    private DropboxUser owner;
    private DbxEntry.WithChildren root;
    private List<BaseFile> files = new ArrayList<BaseFile>();
    private List<BaseFolder> folders = new ArrayList<BaseFolder>();

    public DropboxFolder(DbxEntry.WithChildren root, DropboxUser owner) throws DropboxException {
        this.owner = owner;

        if (root == null) {
            throw new DropboxException("Root is null.");
        } else {
            if (!root.entry.isFolder()) {
                throw new DropboxException("Not a folder.");
            } else {
                this.root = root;
                fetchEntries();
            }
        }
    }

    public List<BaseFile> getFiles() {
        return this.files;
    }

    public List<BaseFolder> getFolders() {
        return this.folders;
    }

    public BaseUser getOwner() {
        return this.owner;
    }

    public String getPath() {
        return this.root.entry.asFolder().path;
    }

    public String getName() {
        return this.root.entry.asFolder().name;
    }

    private void fetchEntries() throws DropboxException {
        for (DbxEntry entry : this.root.children) {
            if (entry.isFile()) {
                this.files.add(new DropboxFile(entry, this.owner));
            } else if (entry.isFolder()) {
                DbxEntry.WithChildren entryWithChildren = this.owner.getEntryChildren(entry.asFolder().path);

                if (entryWithChildren != null) {
                    this.folders.add(new DropboxFolder(entryWithChildren, this.owner));
                }
            }
        }
    }
}
