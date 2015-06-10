package omnidrive.filesystem.sync;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.entry.Blob;
import omnidrive.filesystem.entry.Tree;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.watcher.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public void create(Blob blob) throws Exception {
//        BaseAccount account = uploadStrategy.selectAccount();
//        String newId = account.uploadFile(blob.getId(), blob.getInputStream(), blob.getSize());
//        blob.setId(newId);
//        manifest.add(account, blob);
//        syncManifest();
    }

    public void create(File file) throws Exception {
        if (file.isFile()) {
            createFile(file);
        } else if (file.isDirectory()) {
            createDir(file);
        }
    }

    private void createFile(File file) throws Exception {
        long size = file.length();
        BaseAccount account = uploadStrategy.selectAccount();
        UUID uuid = UUID.randomUUID();
        String id = account.uploadFile(uuid.toString(), new FileInputStream(file), size);
        Blob blob = new Blob(id, size);
        manifest.add(account, blob);
        syncManifest();
    }

    private void createDir(File dir) {
        UUID uuid = UUID.randomUUID();
        Tree tree = new Tree(uuid.toString());
        manifest.add(tree);
    }

    public void modify(File file) throws Exception {

    }

    public void delete(File file) throws Exception {

    }

    private void syncManifest() {
        for (BaseAccount account : accountsManager.getActiveAccounts()) {
            manifest.sync(account);
        }
    }

}
