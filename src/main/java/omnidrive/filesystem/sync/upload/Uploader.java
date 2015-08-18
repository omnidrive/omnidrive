package omnidrive.filesystem.sync.upload;

import omnidrive.api.account.Account;
import omnidrive.filesystem.manifest.entry.Blob;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class Uploader {

    private final UploadStrategy uploadStrategy;

    public Uploader(UploadStrategy uploadStrategy) {
        this.uploadStrategy = uploadStrategy;
    }

    public Blob upload(File file) throws Exception {
        long size = file.length();
        Account account = uploadStrategy.selectAccount(file);
        String id = account.uploadFile(randomId(), new FileInputStream(file), size);

        return new Blob(id, size, account.getType());
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }

}
