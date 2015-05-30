package omnidrive.filesystem.entry;

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

}
