package omnidrive.api.box;

import com.box.sdk.BoxAPIConnection;
import omnidrive.api.base.BaseFolder;

public class BoxFolder extends BaseFolder {

    com.box.sdk.BoxFolder folder;

    public BoxFolder(BoxUser owner, BoxAPIConnection connection, com.box.sdk.BoxFolder.Info info) {
        super(owner);
        this.folder = new com.box.sdk.BoxFolder(connection, info.getID());
    }

    public final String getName() {
        return this.folder.getInfo().getName();
    }

    public final String getPath() {
        return this.folder.getID();
    }
}
