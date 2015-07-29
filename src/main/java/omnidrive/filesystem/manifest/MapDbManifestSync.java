package omnidrive.filesystem.manifest;

import omnidrive.api.base.CloudAccount;
import org.mapdb.DB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MapDbManifestSync implements ManifestSync {

    final private File file;

    final private DB db;

    public MapDbManifestSync(File file, DB db) {
        this.file = file;
        this.db = db;
    }

    public void uploadToAll(List<CloudAccount> accounts) throws Exception {
        commitChanges();
        for (CloudAccount account : accounts) {
            account.updateManifest(getInputStream(), getSize());
        }
    }

    public void uploadToAccount(CloudAccount account) throws Exception {
        commitChanges();
        account.updateManifest(getInputStream(), getSize());
    }

    private void commitChanges() {
        db.commit();
        db.compact();
    }

    private InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    private long getSize() {
        return file.length();
    }

}
