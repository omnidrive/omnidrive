package omnidrive.stub;

import omnidrive.api.base.BaseAccount;
import omnidrive.api.base.BaseException;

import java.io.InputStream;
import java.io.OutputStream;

public class Account extends BaseAccount {

    @Override
    protected void createRootFolder() throws BaseException {

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
    public void deleteFile(String fileId) throws BaseException {
    }

    @Override
    public long downloadFile(String fileId, OutputStream outputStream) throws BaseException {
        return 0;
    }

    @Override
    public void removeFile(String fileId) throws BaseException {

    }

    @Override
    public void removeFolder(String fileId) throws BaseException {

    }

    @Override
    public long getQuotaUsedSize() throws BaseException {
        return 0;
    }

    @Override
    public long getQuotaTotalSize() throws BaseException {
        return 0;
    }

}
