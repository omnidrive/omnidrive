package omnidrive.api.base;

import java.util.Date;

public abstract class BaseFile {

    private final BaseAccount owner;

    public BaseFile(BaseAccount owner) {
        this.owner = owner;
    }

    public BaseAccount getOwner() {
        return this.owner;
    }

    public abstract String getName();


    public abstract String getPath();

    public abstract String getId();


    public abstract long getSize();


    public abstract Date getLastModified();

}
