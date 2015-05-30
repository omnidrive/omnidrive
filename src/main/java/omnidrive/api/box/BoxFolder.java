package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;
import omnidrive.api.base.BaseFolder;

public class BoxFolder extends BaseFolder {

    private final com.box.sdk.BoxFolder folder;

    public BoxFolder(BoxAccount owner, BoxAPIConnection connection, com.box.sdk.BoxFolder.Info info) {
        super(owner);
        this.folder = new com.box.sdk.BoxFolder(connection, info.getID());
    }

    public final String getName() {
        return this.folder.getInfo().getName();
    }

    public final String getId() {
        return this.folder.getID();
    }

    public final String getPath() {
        return null;
    }
}
