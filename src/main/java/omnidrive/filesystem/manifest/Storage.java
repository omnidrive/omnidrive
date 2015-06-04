package omnidrive.filesystem.manifest;

import omnidrive.filesystem.entry.BlobMetadata;
import omnidrive.filesystem.entry.TreeMetadata;

public interface Storage {

    void put(String id, TreeMetadata metadata);

    void put(String id, BlobMetadata metadata);

    TreeMetadata getTreeMetadata(String id);

    BlobMetadata getBlobMetadata(String id);

    void commit();

}
