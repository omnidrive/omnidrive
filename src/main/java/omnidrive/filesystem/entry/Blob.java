package omnidrive.filesystem.entry;

import java.io.File;
import java.util.UUID;

public class Blob implements Entry {

    private final UUID id;

    public Blob(UUID id) {
        this.id = id;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public EntryMetadata getMetadata() {
        return null;
    }

    public static Blob from(File file) {
        return null;
    }
}
