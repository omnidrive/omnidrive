package omnidrive.filesystem.manifest;

import java.io.Serializable;

public interface Storage {

    void put(String id, Serializable metadata);

    void commit();

}
