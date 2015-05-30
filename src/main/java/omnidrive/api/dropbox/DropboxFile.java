package omnidrive.api.dropbox;

import com.dropbox.core.DbxEntry;
import omnidrive.api.base.*;

import java.util.Date;

public class DropboxFile extends BaseFile {

    private final DbxEntry entry;

    public DropboxFile(DbxEntry entry, DropboxAccount owner) throws DropboxException {
        super(owner);

        if (!entry.isFile()) {
            throw new DropboxException("Not a file.");
        } else {
            this.entry = entry;
        }
    }

    @Override
    public final String getPath() {
        return this.entry.asFile().path;
    }

    @Override
    public final String getId() {
        return null;
    }

    @Override
    public final String getName() {
        return this.entry.asFile().name;
    }

    @Override
    public final Date getLastModified() {
        return this.entry.asFile().lastModified;
    }

    @Override
    public final long getSize() {
        return this.entry.asFile().numBytes;
    }
}
