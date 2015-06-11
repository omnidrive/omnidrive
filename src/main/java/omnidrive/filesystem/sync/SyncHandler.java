package omnidrive.filesystem.sync;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.filesystem.watcher.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class SyncHandler implements Handler {

    private final Manifest manifest;

    private final UploadStrategy uploadStrategy;

    private final AccountsManager accountsManager;

    @Inject
    public SyncHandler(Manifest manifest,
                       UploadStrategy uploadStrategy,
                       AccountsManager accountsManager) {
        this.manifest = manifest;
        this.uploadStrategy = uploadStrategy;
        this.accountsManager = accountsManager;
    }

    public String create(File file) throws Exception {
        String id = null;
        if (file.isFile()) {
            id = createFile(file);
        } else if (file.isDirectory()) {
            id = createDir();
        }
        return id;
    }

    public void modify(File file) throws Exception {

    }

    public void delete(File file) throws Exception {

    }

    private String createFile(File file) throws Exception {
        long size = file.length();
        BaseAccount account = uploadStrategy.selectAccount();
        UUID uuid = UUID.randomUUID();
        String id = account.uploadFile(uuid.toString(), new FileInputStream(file), size);
        Blob blob = new Blob(id, size, account.getName());
        manifest.put(blob);

        // update parent
        Tree root = manifest.getRoot();
        root.getItems().add(new TreeItem(id, file.getName()));
        manifest.put(root);

        syncManifest();

        return id;
    }

    private String createDir() {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        Tree tree = new Tree(id);
        manifest.put(tree);

        return id;
    }

    private void syncManifest() {
//        for (BaseAccount account : accountsManager.getActiveAccounts()) {
//            manifest.commit();
//            manifest.sync(account);
//        }
    }

}
