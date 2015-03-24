package omnidrive.Dropbox;

import com.dropbox.core.DbxEntry;
import omnidrive.OmniBase.OmniFile;
import omnidrive.OmniBase.OmniFolder;
import omnidrive.OmniBase.OmniUser;

import java.util.ArrayList;
import java.util.List;


public class DropboxFolder implements OmniFolder {

    private DropboxUser owner;
    private DbxEntry.WithChildren root;
    private List<OmniFile> files = new ArrayList<OmniFile>();
    private List<OmniFolder> folders = new ArrayList<OmniFolder>();

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

    public List<OmniFile> getFiles() {
        return this.files;
    }

    public List<OmniFolder> getFolders() {
        return this.folders;
    }

    public OmniUser getOwner() {
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
