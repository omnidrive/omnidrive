package omnidrive.api.base;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFolder {

    private final BaseAccount owner;

    protected final List<BaseFile> files = new ArrayList<BaseFile>();
    protected final List<BaseFolder> folders = new ArrayList<BaseFolder>();

    public BaseFolder(BaseAccount owner) {
        this.owner = owner;
    }

    public abstract String getName();


    public abstract String getPath();


    public abstract String getId();


    public BaseAccount getOwner() {
        return this.owner;
    }


    public List<BaseFolder> getFolders() {
        return this.folders;
    }


    public List<BaseFile> getFiles() {
        return this.files;
    }

}
