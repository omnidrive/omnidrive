package omnidrive.filesystem.sync;

import omnidrive.algo.SimpleVisitor;
import omnidrive.algo.TreeWalker;
import omnidrive.algo.Visitor;
import omnidrive.api.base.Account;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.exception.InvalidFileException;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.manifest.sync.ManifestSync;
import omnidrive.filesystem.manifest.entry.Blob;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;
import omnidrive.filesystem.manifest.entry.TreeItem;
import omnidrive.filesystem.sync.upload.Uploader;
import omnidrive.filesystem.watcher.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.UUID;

public class SyncHandler implements Handler {

    private final Path root;

    private final Manifest manifest;

    private final ManifestSync manifestSync;

    private final AccountsManager accountsManager;

    private final Uploader uploader;

    private final TreeWalker<EntryNode> walker;

    private final Visitor<EntryNode> removeVisitor = new RemoveVisitor();

    public SyncHandler(Path root,
                       Manifest manifest,
                       ManifestSync manifestSync,
                       Uploader uploader,
                       AccountsManager accountsManager) {
        this.root = root;
        this.manifest = manifest;
        this.manifestSync = manifestSync;
        this.accountsManager = accountsManager;
        this.uploader = uploader;
        walker = new TreeWalker<>(removeVisitor);

    }

    public String create(File file) throws Exception {
        String id;
        if (file.isFile()) {
            id = createFile(file);
        } else if (file.isDirectory()) {
            id = createDir(file);
        } else {
            throw new InvalidFileException();
        }
        uploadManifestToAllAccounts();
        return id;
    }

    public String modify(File file) throws Exception {
        if (!file.isFile()) {
            throw new InvalidFileException();
        }
        Blob blob = getBlob(file);
        String id = blob.getId();
        Blob updated = new Blob(id, file.length(), blob.getAccount());
        Account account = getAccount(blob);
        account.updateFile(id, new FileInputStream(file), updated.getSize());
        manifest.put(updated);
        uploadManifestToAllAccounts();
        return id;
    }

    public void delete(File file) throws Exception {
        Path path = file.toPath();
        Tree parent = findParent(path);
        TreeItem item = parent.getItem(file.getName());
        if (item == null) {
            throw new InvalidFileException();
        }

        String id = item.getId();
        Entry entry = manifest.get(id);
        EntryNode node = new EntryNode(manifest, root.relativize(path), entry);
        walker.walk(node);
        removeVisitor.postVisit(node);

        parent.removeItem(id);
        manifest.put(parent);
        uploadManifestToAllAccounts();
    }

    private void uploadManifestToAllAccounts() throws Exception {
        manifestSync.uploadToAll(accountsManager.getActiveAccounts());
    }

    private String createFile(File file) throws Exception {
        Blob blob = uploader.upload(file);
        String id = blob.getId();
        manifest.put(blob);
        addEntryToParent(file, Entry.Type.BLOB, id);
        return id;
    }

    private String createDir(File file) {
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
            return root.relativize(file).getParent();
        } catch (Exception e) {
            return null;
        }
    }

    private Account getAccount(Blob blob) {
        return accountsManager.getAccount(blob.getAccount());
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
