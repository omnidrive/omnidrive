package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.entry.BlobMetadata;

import java.nio.file.Path;

public class Manifest {

    public static final String ROOT_KEY = "root";

    private final Path root;

    private final Storage storage;

    @Inject
    public Manifest(Path root, Storage storage) {
        this.root = root;
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
