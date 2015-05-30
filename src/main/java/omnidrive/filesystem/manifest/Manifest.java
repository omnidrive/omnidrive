package omnidrive.filesystem.manifest;

import omnidrive.filesystem.entry.Blob;

import java.io.File;

public class Manifest {

    private final Storage storage;

    public Manifest(Storage storage) {
        this.storage = storage;
    }

    public void add(File file) {
        Blob blob = Blob.from(file);
        storage.put(blob.getId(), blob.getMetadata());

    }

}
