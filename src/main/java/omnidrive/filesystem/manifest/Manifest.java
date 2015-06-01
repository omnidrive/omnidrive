package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.entry.Blob;

public class Manifest {

    private final Storage storage;

    @Inject
    public Manifest(Storage storage) {
        this.storage = storage;
    }

    public void add(Blob blob) {
//        Blob blob = Blob.from(fileId, file);
//        storage.put(blob.getId(), blob.getMetadata());
        storage.commit();
    }

    public void sync(BaseAccount account) {

    }

}
