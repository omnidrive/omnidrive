package omnidrive.filesystem.sync;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.filesystem.watcher.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.UUID;

public class SyncHandler implements Handler {

    private final Path root;

    private final Manifest manifest;

    private final UploadStrategy uploadStrategy;

    private final AccountsManager accountsManager;

    @Inject
    public SyncHandler(Path root,
                       Manifest manifest,
                       UploadStrategy uploadStrategy,
                       AccountsManager accountsManager) {
        this.root = root;
        this.manifest = manifest;
        this.uploadStrategy = uploadStrategy;
        this.accountsManager = accountsManager;
    }

    public String create(File file) throws Exception {
        String id = null;
        if (file.isFile()) {
            id = createFile(file);
        } else if (file.isDirectory()) {
            id = createDir(file);
        }
        syncManifest();
        return id;
    }

    public void modify(File file) throws Exception {
        // TODO
    }

    public void delete(File file) throws Exception {
        // TODO
    }

    private String createFile(File file) throws Exception {
        long size = file.length();
        BaseAccount account = uploadStrategy.selectAccount();
        String id = account.uploadFile(randomId(), new FileInputStream(file), size);
        manifest.put(new Blob(id, size, account.getName()));
        updateParent(file, Entry.Type.BLOB, id);
        return id;
    }

    private String createDir(File file) {
        String id = randomId();
        manifest.put(new Tree(id));
        updateParent(file, Entry.Type.TREE, id);
        return id;
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }

    private void updateParent(File file, Entry.Type type, String id) {
        Tree parent = findParent(file.toPath());
        parent.addItem(new TreeItem(type, id, file.getName()));
        manifest.put(parent);
    }

    private Tree findParent(Path file) {
        Tree current = manifest.getRoot();
        Path relative = root.relativize(file).getParent();
        if (relative != null) {
            for (Path part : relative) {
                TreeItem item = current.getItem(part.toString());
                current = manifest.getTree(item.getId());
            }
        }
        return current;
    }

    private void syncManifest() throws Exception {
        manifest.sync(accountsManager.getActiveAccounts());
    }

}
