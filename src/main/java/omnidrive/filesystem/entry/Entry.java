package omnidrive.filesystem.entry;

import java.util.UUID;

public interface Entry {

    UUID getId();

    EntryMetadata getMetadata();

}
