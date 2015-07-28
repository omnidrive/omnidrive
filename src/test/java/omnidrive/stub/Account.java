package omnidrive.stub;

import com.google.api.client.util.ArrayMap;
import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class Account extends BaseAccount {

    public static final int DEFAULT_CAPACITY = 100;

    final private long totalSize;

    private long usedSize = 0;

    final private Map<String, String> files = new ArrayMap<String, String>();

    public Account() {
        this(DEFAULT_CAPACITY);
    }

    public Account(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    protected void createRootFolder() throws BaseException {

    }

    @Override
    public String getOmniDriveFolderId() throws BaseException {
        return null;
    }

    @Override
    public String getUsername() throws BaseException {
        return null;
    }

    @Override
    public String getUserId() throws BaseException {
        return null;
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) throws BaseException {
        return null;
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws BaseException {
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
    public void removeFile(String fileId) throws BaseException {

    }

    @Override
    public void removeFolder(String fileId) throws BaseException {

    }

    @Override
    public void updateFile(String fileId, InputStream inputStream, long size) throws BaseException {

    }

    @Override
    public long downloadManifestFile(OutputStream outputStream) throws BaseException {
        return 0;
    }

    @Override
    public long getQuotaUsedSize() throws BaseException {
        return usedSize;
    }

    @Override
    public long getQuotaTotalSize() throws BaseException {
        return totalSize;
    }

    public void addFile(String fileId, String fileContents) {
        if (!files.containsKey(fileId)) {
            usedSize -= fileContents.length();
            files.put(fileId, fileContents);
        }
    }

}
