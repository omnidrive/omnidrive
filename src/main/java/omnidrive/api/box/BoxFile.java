package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;

import omnidrive.api.base.BaseFile;

import java.util.Date;

public class BoxFile extends BaseFile {

    private final com.box.sdk.BoxFile file;

    public BoxFile(BoxAccount owner, BoxAPIConnection connection, com.box.sdk.BoxFile.Info info) {
        super(owner);
        this.file = new com.box.sdk.BoxFile(connection, info.getID());
    }

    @Override
    public final String getName() {
        return this.file.getInfo().getName();
    }

    @Override
    public final String getPath() {
        return null;
    }

    @Override
    public final String getId() {
        return this.file.getInfo().getID();
    }

    @Override
    public final long getSize() {
        return this.file.getInfo().getSize();
    }

    @Override
    public final Date getLastModified() {
        return this.file.getInfo().getModifiedAt();
    }
}
