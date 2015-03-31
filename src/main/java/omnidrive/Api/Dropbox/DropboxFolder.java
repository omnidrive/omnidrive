package omnidrive.Api.Dropbox;

import com.dropbox.core.DbxEntry;
import omnidrive.Api.Base.BaseFile;
import omnidrive.Api.Base.BaseFolder;
import omnidrive.Api.Base.BaseUser;

import java.util.ArrayList;
import java.util.List;


public class DropboxFolder implements BaseFolder {

    private DropboxUser owner;
    private DbxEntry entry;
    private List<BaseFile> files = new ArrayList<BaseFile>();
    private List<BaseFolder> folders = new ArrayList<BaseFolder>();

    public DropboxFolder(DbxEntry.WithChildren entryWithChildren, DropboxUser owner) throws DropboxException {
        this.owner = owner;

        if (entryWithChildren == null) {
            throw new DropboxException("Entry is null.");
        } else if (!entryWithChildren.entry.isFolder()) {
            throw new DropboxException("Not a folder.");
        } else {
            this.entry = entryWithChildren.entry;
            fetchEntries(entryWithChildren);
        }
    }

    private DropboxFolder(DbxEntry entry, DropboxUser owner) throws DropboxException {
        if (entry == null) {
            throw new DropboxException("Entry is null.");
        } else if (!entry.isFolder()) {
            throw new DropboxException("Not a folder.");
        } else {
            this.entry = entry;
            this.owner = owner;
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
        return this.entry.asFolder().path;
    }

    public String getName() {
        return this.entry.asFolder().name;
    }

    private void fetchEntries(DbxEntry.WithChildren entryWithChildren) throws DropboxException {
        for (DbxEntry entry : entryWithChildren.children) {
            if (entry.isFile()) {
                this.files.add(new DropboxFile(entry, this.owner));
            } else if (entry.isFolder()) {
                this.folders.add(new DropboxFolder(entry, this.owner));
            }
        }
    }
}
