package omnidrive.filesystem.entry;

import java.io.File;
import java.io.Serializable;

public class Blob implements Entry {

    private final String id;

    public Blob(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Serializable getMetadata() {
        return null;
    }

    public static Blob from(String fileId, File file) {
        return new Blob(fileId);
    }

}
