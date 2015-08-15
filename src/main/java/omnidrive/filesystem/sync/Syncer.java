package omnidrive.filesystem.sync;

import omnidrive.algo.Comparator;
import omnidrive.algo.Pair;
import omnidrive.algo.TreeDiff;
import omnidrive.api.base.AccountType;
import omnidrive.api.base.Account;
import omnidrive.api.managers.AccountsManager;
import omnidrive.app.ManifestFilter;
import omnidrive.filesystem.exception.UnableToDeleteFileException;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class Syncer {

    final private Path rootPath;

    final private AccountsManager accountsManager;

    public Syncer(Path rootPath, AccountsManager accountsManager) {
        this.rootPath = rootPath;
        this.accountsManager = accountsManager;
    }

    public void fullSync(Manifest manifest) throws Exception {
        Comparator<FileNode, EntryNode> comparator = new SyncComparator();
        TreeDiff<FileNode, EntryNode> diff = new TreeDiff<>(comparator);
        FileNode left = new FileNode(rootPath.toFile(), new ManifestFilter());
        EntryNode right = EntryNode.getRoot(manifest);
        TreeDiff.Result<FileNode, EntryNode> result = diff.run(left, right);
        syncDiffResult(result, manifest);
    }

    private void syncDiffResult(TreeDiff.Result<FileNode, EntryNode> result, Manifest manifest) throws Exception {
        long manifestUpdateTime = manifest.getUpdatedTime();
        for (FileNode fileNode : result.addedLeft()) {
            // New entries in filesystem that don't exist in latest manifest
            File file = fileNode.getFile();
            if (file.lastModified() > manifestUpdateTime) {
                upload(file);
            } else {
                delete(file);
            }
        }
        for (EntryNode entry : result.addedRight()) {
            // New entries in manifest that don't exist in filesystem
            download(entry);
        }
        for (Pair<FileNode, EntryNode> pair : result.modified()) {
            // Entries that exist in both filesystem and manifest but were modified
            File file = pair.getLeft().getFile();
            if (file.lastModified() > manifestUpdateTime) {
                download(pair.getRight());
            }
        }
    }

    private void upload(File file) {
        System.out.println("Upload " + file);
    }

    private void delete(File file) throws Exception {
        if (!file.delete()) {
            throw new UnableToDeleteFileException(file);
        }
    }

    private void download(EntryNode entry) throws Exception {
        Path path = entry.getPath();
        if (entry.getType() == Entry.Type.BLOB) {
            download(entry.as(Blob.class), path);
        } else {
            download(entry.as(Tree.class), path);
        }
    }

    private void download(Blob blob, Path path) throws Exception {
        AccountType accountType = blob.getAccount();
        Account account = accountsManager.getAccount(accountType);
        File file = rootPath.resolve(path).toFile();
        OutputStream outputStream = new FileOutputStream(file);
        account.downloadFile(blob.getId(), outputStream);
    }

    private void download(Tree tree, Path path) throws Exception {

    }

//    private void download(Path path, TreeItem item) throws Exception {
//        Path downloadPath = path.resolve(item.getName());
//        File file = downloadPath.toFile();
//        if (!file.exists() || file.lastModified() < item.getLastModified()) {
//            OutputStream outputStream = new FileOutputStream(file);
//            account.downloadFile(item.getId(), outputStream);
//        }
//    }

//    private boolean createDir(Path path) {
//        File dir = path.toFile();
//        return dir.mkdir();
//    }

    private class SyncComparator implements Comparator<FileNode, EntryNode> {

        @Override
        public boolean areEqual(FileNode left, EntryNode right) {
            return true;
        }

    }

}
