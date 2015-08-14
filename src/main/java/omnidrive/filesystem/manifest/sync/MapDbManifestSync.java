package omnidrive.filesystem.manifest.sync;

import com.google.common.io.Files;
import omnidrive.api.base.AccountException;
import omnidrive.api.base.CloudAccount;
import omnidrive.filesystem.FileSystem;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.util.MapDbUtils;
import org.mapdb.DB;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class MapDbManifestSync implements ManifestSync {

    final private MapDbManifestArchiver archiver;

    final private DB db;

    public MapDbManifestSync(File file, DB db) {
        archiver = new MapDbManifestArchiver(file);
        this.db = db;
    }

    public void uploadToAll(List<CloudAccount> accounts) throws Exception {
        commitChanges();
        File archive = archiver.archive();
        long size = archive.length();
        for (CloudAccount account : accounts) {
            account.updateManifest(new FileInputStream(archive), size);
        }
        assert archive.delete();
    }

    public void uploadToAccount(CloudAccount account) throws Exception {
        commitChanges();
        File archive = archiver.archive();
        account.updateManifest(new FileInputStream(archive), archive.length());
        assert archive.delete();
    }

    public Manifest downloadFromAccount(CloudAccount account) throws Exception {
        File tempDir = Files.createTempDir();
        Path tempDirPath = tempDir.toPath();
        File tar = File.createTempFile("manifest", "tar", tempDir);

        downloadManifest(account, tar);
        archiver.extract(tar, tempDirPath);
        assert tar.delete();

        return getManifest(tempDirPath);
    }

    private void commitChanges() throws IOException {
        db.commit();
        db.compact();
    }

    private void downloadManifest(CloudAccount account, File tar) throws AccountException, IOException {
        OutputStream outputStream = new FileOutputStream(tar);
        account.downloadManifest(outputStream);
        outputStream.close();
    }

    private Manifest getManifest(Path dir) {
        Path manifestPath = dir.resolve(FileSystem.MANIFEST_FILENAME);
        DB db = MapDbUtils.createFileDb(manifestPath.toFile());

        return new MapDbManifest(db);
    }

}
