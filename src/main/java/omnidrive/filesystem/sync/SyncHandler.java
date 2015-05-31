package omnidrive.filesystem.sync;

import com.google.inject.Inject;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;
import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.watcher.Handler;

import java.io.*;

public class SyncHandler implements Handler {

    private final Manifest manifest;

    private final UploadStrategy uploadStrategy;

    private final AccountsManager accountsManager = AccountsManager.getAccountsManager();

    @Inject
    public SyncHandler(Manifest manifest, UploadStrategy uploadStrategy) {
        this.manifest = manifest;
        this.uploadStrategy = uploadStrategy;
    }

    @Override
    public void create(File file) {
        if (file.isFile()) {
            try {
                createFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void modify(File file) {

    }

    @Override
    public void delete(File file) {

    }

    private void createFile(File file) throws FileNotFoundException, BaseException {
        BaseAccount account = uploadStrategy.selectAccount();
        String fileId = account.uploadFile(file.getName(), new FileInputStream(file), file.length());
        manifest.add(fileId, file);
        syncManifest();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeObject(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void syncManifest() {
        for (BaseAccount account : accountsManager.getActiveAccounts()) {
//            account.uploadFile()
        }
    }

}
