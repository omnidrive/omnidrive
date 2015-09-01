package omnidrive.sync.diff;

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

public class Diff implements Runnable {

    private final Manifest manifest;
    private final Path root;
    private final AccountsManager accountsManager;
    private final DiffFilter filter;

    public Diff(Manifest manifest, Path root, AccountsManager accountsManager, DiffFilter filter) {
        this.manifest = manifest;
        this.root = root;
        this.accountsManager = accountsManager;
        this.filter = filter;
    }

    public void solve() throws Exception {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Started diff process...");
            walkFolder(root, manifest.getRoot().getItems());
            System.out.println("Diff process done.");
        } catch (Exception ex) {
            System.out.println("Diff Error: " + ex.getMessage());
        }
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
            updateFilter(file.toPath());
            if (file.createNewFile()) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                account.downloadFile(id, fileOutputStream);
                fileOutputStream.close();
            }
        }
    }

    private void createFolder(File folder) throws Exception {
        if (!folder.exists()) {
            updateFilter(folder.toPath());
            if (!folder.mkdir()) {
                throw new Exception("Failed to create folder: " + folder.getPath());
            }
        }
    }

    private void updateFilter(Path path) {
        if (filter != null) {
            filter.update(path);
        }
    }
}
