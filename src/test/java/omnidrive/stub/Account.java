package omnidrive.stub;

import com.google.api.client.util.ArrayMap;
import omnidrive.api.account.AccountException;
import omnidrive.api.account.AccountMetadata;
import omnidrive.api.account.AccountType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class Account extends omnidrive.api.account.Account {

    public static final int DEFAULT_CAPACITY = 100;

    final private long totalSize;

    private long usedSize = 0;

    final private Map<String, String> files = new ArrayMap<String, String>();

    public Account(AccountType type) {
        this(type, DEFAULT_CAPACITY);
    }

    public Account(AccountType type, long totalSize) {
        super(type, new AccountMetadata("client_id", "client_secret"));
        this.totalSize = totalSize;
    }

    @Override
    protected void createRootFolder() throws AccountException {

    }

    @Override
    public String getOmniDriveFolderId() throws AccountException {
        return null;
    }

    @Override
    public String getUsername() throws AccountException {
        return null;
    }

    @Override
    public String getUserId() throws AccountException {
        return null;
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) throws AccountException {
        return null;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws AccountException {
        if (files.containsKey(fileId)) {
            try {
                String content = files.get(fileId);
                outputStream.write(content.getBytes());
                return content.length();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public void removeFile(String fileId) throws AccountException {

    }

    @Override
    public void removeFolder(String fileId) throws AccountException {

    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws AccountException {

    }

    @Override
    public long downloadManifest(OutputStream outputStream) throws AccountException {
        return 0;
    }

    @Override
    public void uploadManifest(InputStream inputStream, long size) throws AccountException {

    }

    @Override
    public void removeManifest() throws AccountException {

    }

    @Override
    public void fetchManifestId() throws AccountException {

    }

    @Override
    public long getQuotaUsedSize() throws AccountException {
        return usedSize;
    }

    @Override
    public long getQuotaTotalSize() throws AccountException {
        return totalSize;
    }

    public void addFile(String fileId, String fileContents) {
        if (!files.containsKey(fileId)) {
            usedSize -= fileContents.length();
            files.put(fileId, fileContents);
        }
    }

}
