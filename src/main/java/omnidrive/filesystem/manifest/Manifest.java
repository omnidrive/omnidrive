package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import omnidrive.filesystem.entry.Blob;

import java.io.File;

public class Manifest {

    private final Storage storage;

    @Inject
    public Manifest(Storage storage) {
        this.storage = storage;
    }

    public void add(String fileId, File file) {
        Blob blob = Blob.from(fileId, file);
        storage.put(blob.getId(), blob.getMetadata());
        storage.commit();
    }

}
