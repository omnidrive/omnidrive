package omnidrive.filesystem.sync;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class Syncer {

    final private Path rootPath;

    final private BaseAccount account;

    public Syncer(Path rootPath, BaseAccount account) {
        this.rootPath = rootPath;
        this.account = account;
    }

    public void fullSync(Manifest manifest) throws Exception {
        syncDir(manifest.getRoot(), rootPath);
    }

    private void syncDir(Tree tree, Path path) throws Exception {
        for (TreeItem item : tree.getItems()) {
            if (item.getType() == Entry.Type.BLOB) {
                download(path, item);
            }
        }
    }

    private void download(Path path, TreeItem item) throws Exception {
        Path downloadPath = path.resolve(item.getName());
        OutputStream outputStream = new FileOutputStream(downloadPath.toFile());
        account.downloadFile(item.getId(), outputStream);
    }

}
