package omnidrive.api.dropbox;

import com.dropbox.core.DbxEntry;
import omnidrive.api.base.BaseFolder;


public class DropboxFolder extends BaseFolder {

    private final DbxEntry entry;

    public DropboxFolder(DbxEntry.WithChildren entryWithChildren, DropboxAccount owner) throws DropboxException {
        super(owner);

        if (entryWithChildren == null) {
            throw new DropboxException("Entry is null.");
        } else if (!entryWithChildren.entry.isFolder()) {
            throw new DropboxException("Not a folder.");
        } else {
            this.entry = entryWithChildren.entry;
            fetchEntries(entryWithChildren);
        }
    }

    private DropboxFolder(DbxEntry entry, DropboxAccount owner) throws DropboxException {
        super(owner);

        if (entry == null) {
            throw new DropboxException("Entry is null.");
        } else if (!entry.isFolder()) {
            throw new DropboxException("Not a folder.");
        } else {
            this.entry = entry;
        }
    }

    @Override
    public final String getPath() {
        return this.entry.asFolder().path;
    }

    @Override
    public final String getId() {
        return null;
    }

    @Override
    public final String getName() {
        return this.entry.asFolder().name;
    }

    private void fetchEntries(DbxEntry.WithChildren entryWithChildren) throws DropboxException {
        for (DbxEntry entry : entryWithChildren.children) {
            DropboxAccount owner = (DropboxAccount)getOwner();
            if (entry.isFile()) {
                this.files.add(new DropboxFile(entry, owner));
            } else if (entry.isFolder()) {
                this.folders.add(new DropboxFolder(entry, owner));
            }
        }
    }
}
