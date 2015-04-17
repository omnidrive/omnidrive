package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;

import omnidrive.api.base.BaseFile;

import java.util.Date;

public class BoxFile extends BaseFile {

    com.box.sdk.BoxFile file;

    public BoxFile(BoxUser owner, BoxAPIConnection connection, com.box.sdk.BoxFile.Info info) {
        super(owner);
        this.file = new com.box.sdk.BoxFile(connection, info.getID());
    }

    public final String getName() {
        return this.file.getInfo().getName();
    }

    public final String getPath() {
        return this.file.getInfo().getID();
    }

    public final long getSize() {
        return this.file.getInfo().getSize();
    }

    public final Date getLastModified() {
        return this.file.getInfo().getModifiedAt();
    }
}
