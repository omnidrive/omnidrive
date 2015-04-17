package omnidrive.api.base;

import java.util.Date;

public abstract class BaseFile {

    private final BaseUser owner;

    public BaseFile(BaseUser owner) {
        this.owner = owner;
    }

    public BaseUser getOwner() {
        return this.owner;
    }

    public abstract String getName();


    public abstract String getPath();

    public abstract String getId();


    public abstract long getSize();


    public abstract Date getLastModified();

}
