package omnidrive.api.base;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class BaseAccount {

    protected static final String ROOT_FOLDER_NAME = ".omnidrive";
    protected static final String ROOT_FOLDER_PATH = "/" + ROOT_FOLDER_NAME;

    protected String getFullRootFolderPath() {
        return ROOT_FOLDER_PATH + "/";
    }

    protected String getFullPath(String name) {
        return getFullRootFolderPath() + name;
    }

    public void initialize() throws BaseException {
        createRootFolder();
    }

    protected abstract void createRootFolder() throws BaseException;

    public abstract String getUsername() throws BaseException;

    public abstract String getUserId() throws BaseException;

    public abstract String uploadFile(String name, InputStream inputStream, long size) throws BaseException;

    public abstract long downloadFile(String fileId, OutputStream outputStream) throws BaseException;

    public abstract void removeFile(String fileId) throws BaseException;

    public abstract void removeFolder(String fileId) throws BaseException;

    public abstract long getQuotaUsedSize() throws BaseException;

    public abstract long getQuotaTotalSize() throws BaseException;

    public long getQuotaRemainingSize() throws BaseException {
        return getQuotaTotalSize() - getQuotaUsedSize();
    }

}
