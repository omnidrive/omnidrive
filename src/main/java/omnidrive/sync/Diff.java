package omnidrive.sync;

import omnidrive.api.account.Account;
import omnidrive.api.managers.AccountsManager;
import omnidrive.manifest.Manifest;
import omnidrive.manifest.entry.Blob;
import omnidrive.manifest.entry.Entry;
import omnidrive.manifest.entry.Tree;
import omnidrive.manifest.entry.TreeItem;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;

public class Diff {

    private final Manifest manifest;
    private final Path root;
    private final AccountsManager accountsManager;

    public Diff(Manifest manifest, Path root, AccountsManager accountsManager) {
        this.manifest = manifest;
        this.root = root;
        this.accountsManager = accountsManager;
    }

    public void solve() throws Exception {
        walkFolder(root, manifest.getRoot().getItems());
    }

    private void walkFolder(Path parent, List<TreeItem> items) throws Exception {
        for (int index = 0; index < items.size(); index++) {
            walkFile(parent, items, index);
        }
    }

    private void walkFile(Path parent, List<TreeItem> items, int index) throws Exception {
        if (index < items.size()) {
            TreeItem item = items.get(index);
            if (item.getType() == Entry.Type.BLOB) {
                File file = new File(parent.toString(), item.getName());
                Blob blob = (Blob) manifest.get(item.getId());
                Account account = accountsManager.getAccount(blob.getAccount());
                createFile(account, file, blob.getId());
            } else { //item.getType() == Entry.Type.TREE
                File folder = new File(parent.toString(), item.getName());
                createFolder(folder);
                Tree tree = (Tree) manifest.get(item.getId());
                walkFolder(folder.toPath(), tree.getItems());
            }
        }
    }

    private void createFile(Account account, File file, String id) throws Exception {
        if (!file.exists()) {
            if (file.createNewFile()) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                account.downloadFile(id, fileOutputStream);
                fileOutputStream.close();
            }
        }
    }

    private void createFolder(File folder) throws Exception {
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                throw new Exception("Failed to create folder: " + folder.getPath());
            }
        }
    }
}
