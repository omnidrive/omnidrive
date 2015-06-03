package omnidrive.filesystem.manifest;

import omnidrive.filesystem.entry.TreeMetadata;

import java.io.Serializable;

public interface Storage {

    void put(String id, Serializable metadata);

    TreeMetadata get(String id);

    void commit();

}
