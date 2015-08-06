package omnidrive.filesystem.manifest;

import omnidrive.api.base.CloudAccount;
import omnidrive.api.managers.AccountsManager;
import org.mapdb.DB;

import java.io.File;
import java.io.FileInputStream;

public class MapDbManifestSync implements ManifestSync {

    private static final String UPLOAD_MANIFEST_FILENAME = "manifest";

    final private AccountsManager accountsManager;

    final private File file;

    final private DB db;

    public MapDbManifestSync(AccountsManager accountsManager, File file, DB db) {
        this.accountsManager = accountsManager;
        this.file = file;
        this.db = db;
    }

    public void upload() throws Exception {
        db.commit();
        db.compact();
        long size = file.length();
        for (CloudAccount account : accountsManager.getActiveAccounts()) {
            account.updateFile(UPLOAD_MANIFEST_FILENAME, new FileInputStream(file), size);
        }
    }

}
