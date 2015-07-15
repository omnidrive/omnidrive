package omnidrive.filesystem.sync;

import omnidrive.api.base.BaseAccount;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.filesystem.manifest.walker.ItemVisitor;
import omnidrive.filesystem.manifest.walker.ManifestWalker;
import omnidrive.filesystem.manifest.walker.SimpleVisitor;

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
        ManifestWalker walker = new ManifestWalker(manifest);
        ItemVisitor visitor = new SyncVisitor(rootPath);
        walker.walk(visitor);
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

    private class SyncVisitor extends SimpleVisitor {

        private Path path;

        public SyncVisitor(Path path) {
            this.path = path;
        }

        public void preVisit(TreeItem item) throws Exception {
            createDir(path, item);
            path = path.resolve(item.getName());
        }

        public void visit(TreeItem item) throws Exception {
            download(path, item);
        }

        public void postVisit(TreeItem item) throws Exception {
            path = path.getParent();
        }

    }

}
