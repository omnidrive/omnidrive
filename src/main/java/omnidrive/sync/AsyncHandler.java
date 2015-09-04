package omnidrive.sync;

import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.watcher.Handler;
import omnidrive.manifest.ManifestSync;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

/**
 * Created by assafey on 9/3/15.
 */
public class AsyncHandler implements Handler {

    private final Synchronizer synchronizer;
    private final ManifestSync manifestSync;
    private final AccountsManager accountsManager;

    public AsyncHandler(Synchronizer synchronizer,
                        ManifestSync manifestSync,
                        AccountsManager accountsManager) {
        this.manifestSync = manifestSync;
        this.accountsManager = accountsManager;
        this.synchronizer = synchronizer;
    }

    public String create(File file) throws Exception {
        startProcess(file, StandardWatchEventKinds.ENTRY_CREATE);
        return null;
    }

    public String modify(File file) throws Exception {
        startProcess(file, StandardWatchEventKinds.ENTRY_MODIFY);
        return null;
    }

    public void delete(File file) throws Exception {
        startProcess(file, StandardWatchEventKinds.ENTRY_DELETE);
    }

    private void startProcess(File file, WatchEvent.Kind kind) {
        AsyncHandlerProcessor processor = new AsyncHandlerProcessor(kind, file);
        Thread thread = new Thread(processor);
        thread.setDaemon(true);
        thread.start();
    }

    private void createFile(File file) throws Exception {
        String id = synchronizer.upload(file);
        if (id != null) {
            uploadManifestToAllAccounts();
        }
    }

    private void modifyFile(File file) throws Exception {
        String id = synchronizer.update(file);
        if (id != null) {
            uploadManifestToAllAccounts();
        }
    }

    private void deleteFile(File file) throws Exception {
        boolean deleted = synchronizer.delete(file);
        if (deleted) {
            uploadManifestToAllAccounts();
        }
    }

    private void uploadManifestToAllAccounts() throws Exception {
        manifestSync.uploadToAll(accountsManager.getActiveAccounts());
    }

    private class AsyncHandlerProcessor implements Runnable {

        private final WatchEvent.Kind kind;
        private final File file;

        public AsyncHandlerProcessor(WatchEvent.Kind kind, File file) {
            this.file = file;
            this.kind = kind;
        }

        @Override
        public void run() {
            try {
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    System.out.println("Async Create " + file);
                    createFile(file);
                }
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("Async Modify " + file);
                    modifyFile(file);
                }
                if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("Async Delete " + file);
                    deleteFile(file);
                }
            } catch (Exception ex) {
                System.out.println("AsyncHandler Error: " + ex.getMessage());
            }
        }
    }
}
