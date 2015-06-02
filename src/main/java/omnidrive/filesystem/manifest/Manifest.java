package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.entry.BlobMetadata;

public class Manifest {

    private final Storage storage;

    @Inject
    public Manifest(Storage storage) {
        this.storage = storage;
    }

    public void add(BaseAccount account, Blob blob) {
        BlobMetadata metadata = new BlobMetadata(blob.getSize(), account.getName());
        storage.put(blob.getId(), metadata);
        storage.commit();
    }

    public void sync(BaseAccount account) {

    }

}
