package omnidrive.filesystem.sync;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;

import java.io.File;
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
        syncDir(manifest, manifest.getRoot(), rootPath);
    }

    private void syncDir(Manifest manifest, Tree tree, Path path) throws Exception {
        for (TreeItem item : tree.getItems()) {
            if (item.getType() == Entry.Type.BLOB) {
                download(path, item);
            } else if (item.getType() == Entry.Type.TREE) {
                createDir(path, item);
                syncDir(manifest, manifest.get(item.getId(), Tree.class), path.resolve(item.getName()));
            }
        }
    }

    private void download(Path path, TreeItem item) throws Exception {
        Path downloadPath = path.resolve(item.getName());
        File file = downloadPath.toFile();
        if (!file.exists() || file.lastModified() < item.getLastModified()) {
            OutputStream outputStream = new FileOutputStream(file);
            account.downloadFile(item.getId(), outputStream);
        }
    }

    private boolean createDir(Path path, TreeItem item) {
        Path dirPath = path.resolve(item.getName());
        File dir = dirPath.toFile();
        return dir.mkdir();
    }

}
