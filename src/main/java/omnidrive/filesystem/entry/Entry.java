package omnidrive.filesystem.entry;

import java.io.Serializable;

public interface Entry {

    String getId();

    Serializable getMetadata();

}
