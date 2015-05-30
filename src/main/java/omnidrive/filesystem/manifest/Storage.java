package omnidrive.filesystem.manifest;

import omnidrive.filesystem.entry.EntryMetadata;

import java.util.UUID;

public interface Storage {

    void put(UUID id, EntryMetadata metadata);

}
