package omnidrive.sync;

import omnidrive.algo.*;
import omnidrive.api.account.Account;
import omnidrive.api.account.AccountType;
import omnidrive.api.managers.AccountsManager;
import omnidrive.exceptions.InvalidFileException;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.entry.Blob;
import omnidrive.manifest.entry.Entry;
import omnidrive.manifest.entry.Tree;
import omnidrive.manifest.entry.TreeItem;
import omnidrive.sync.diff.Diff;
import omnidrive.sync.diff.DiffFilter;
import omnidrive.sync.upload.Uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.UUID;

public class Synchronizer {

    final private Path rootPath;

    final private Manifest manifest;

    final private Uploader uploader;

    final private AccountsManager accountsManager;

    final private TreeWalker<EntryNode> walker;

    final private Visitor<EntryNode> removeVisitor = new RemoveVisitor();

    public Synchronizer(Path rootPath,
                        Manifest manifest,
                        Uploader uploader,
                        AccountsManager accountsManager) {
        this.rootPath = rootPath;
        this.manifest = manifest;
        this.uploader = uploader;
        this.accountsManager = accountsManager;
        walker = new TreeWalker<>(removeVisitor);
    }

    public void fullSync(Manifest manifest, DiffFilter filter) throws Exception {
        Diff diff = new Diff(manifest, rootPath, accountsManager, filter);
        diff.solve();
    }

    public String upload(File file) throws Exception {
        if (!file.exists()) {
            return null;
        }

        System.out.println("Upload " + file);

        String id;
        if (file.isFile()) {
            id = uploadFile(file);
        } else if (file.isDirectory()) {
            id = uploadDir(file);
        } else {
            throw new InvalidFileException(file.toPath().toString() + " is not a file and not a dir");
        }

        return id;
    }

    public String update(File file) throws Exception {
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        System.out.println("Update " + file);

        Blob blob = getBlob(file);
        String id = blob.getId();
        Blob updated = new Blob(id, file.length(), blob.getAccount());
        Account account = getAccount(blob);
        account.updateFile(id, new FileInputStream(file), updated.getSize());
        manifest.put(updated);

        return id;
    }

    public boolean delete(File file) throws Exception {
        if (file.exists()) { // file should not exist at this point
            return false;
        }

        System.out.println("Delete " + file);

        Path path = file.toPath();
        Tree parent = findParent(path);
        TreeItem item = parent.getItem(file.getName());
        if (item == null) {
            throw new InvalidFileException("parent missing for " + file.toPath().toString());
        }

        String id = item.getId();
        Entry entry = manifest.get(id);
        EntryNode node = new EntryNode(manifest, rootPath.relativize(path), entry);
        walker.walk(node);
        removeVisitor.postVisit(node);

        parent.removeItem(id);
        manifest.put(parent);

        return true;
    }

    private String uploadFile(File file) throws Exception {
        Blob blob = uploader.upload(file);
        String id = blob.getId();
        manifest.put(blob);
        addEntryToParent(file, Entry.Type.BLOB, id);
        return id;
    }

    private String uploadDir(File file) {
        String id = randomId();
        manifest.put(new Tree(id));
        addEntryToParent(file, Entry.Type.TREE, id);
        return id;
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }

    private void addEntryToParent(File file, Entry.Type type, String id) {
        Tree parent = findParent(file.toPath());
        parent.addItem(new TreeItem(type, id, file.getName(), 0));
        manifest.put(parent);
    }

    private Blob getBlob(File file) {
        Tree parent = findParent(file.toPath());
        TreeItem item = parent.getItem(file.getName());
        return manifest.get(item.getId(), Blob.class);
    }

    private Tree findParent(Path file) {
        Tree current = manifest.getRoot();
        Path relative = getParentPath(file);
        if (relative != null) {
            for (Path part : relative) {
                TreeItem item = current.getItem(part.toString());
                current = manifest.get(item.getId(), Tree.class);
            }
        }
        return current;
    }

    private Path getParentPath(Path file) {
        try {
            return rootPath.relativize(file).getParent();
        } catch (Exception e) {
            return null;
        }
    }

    private Account getAccount(Blob blob) {
        return accountsManager.getAccount(blob.getAccount());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void syncDiffResult(TreeDiff.Result<FileNode, EntryNode> result, Manifest manifest) throws Exception {
        long manifestUpdateTime = manifest.getUpdatedTime();
        for (FileNode fileNode : result.addedLeft()) {
            // New entries in filesystem that don't exist in latest manifest
            File file = fileNode.getFile();
            if (file.lastModified() > manifestUpdateTime) {
                upload(file);
            } else {
                assert file.delete();
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

    private class SyncComparator implements Comparator<FileNode, EntryNode> {

        @Override
        public boolean areEqual(FileNode left, EntryNode right) {
            return true;
        }

    }

    private class RemoveVisitor extends SimpleVisitor<EntryNode> {

        public void visit(EntryNode item) throws Exception {
            if (item.getType() == Entry.Type.BLOB) {
                Blob blob = item.as(Blob.class);
                Account account = getAccount(blob);
                account.removeFile(blob.getId());
                manifest.remove(blob);
            }
        }

        public void postVisit(EntryNode item) throws Exception {
            if (item.getType() == Entry.Type.TREE) {
                manifest.remove(item.as(Tree.class));
            }
        }

    }

}