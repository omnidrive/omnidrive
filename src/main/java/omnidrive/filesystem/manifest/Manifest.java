package omnidrive.filesystem.manifest;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.entry.*;

import java.nio.file.Path;

public class Manifest {

    public static final String ROOT_KEY = "root";

    private final Path root;

    private final Storage storage;

    @Inject
    public Manifest(Path root, Storage storage) {
        this.root = root;
        this.storage = storage;
        initRoot();
    }

    private void initRoot() {
        storage.put(ROOT_KEY, new TreeMetadata());
    }

    public void add(BaseAccount account, Blob blob) {
        BlobMetadata metadata = new BlobMetadata(blob.getSize(), account.getName());
        storage.put(blob.getId(), metadata);
        updateParent(blob);
        storage.commit();
    }

    public void add(BaseAccount account, Tree tree) {
        TreeMetadata metadata = new TreeMetadata();
    }

    private void updateParent(Blob blob) {
        TreeMetadata metadata = storage.getTreeMetadata(ROOT_KEY);
        metadata.items.add(new TreeItem(blob.getId(), blob.getName()));
    }

    public void sync(BaseAccount account) {
        // TODO
    }

}
