package omnidrive.api.managers;

import omnidrive.api.base.BaseException;

import java.io.FileOutputStream;

public class DownloadManager {

    // will unite the file parts into one file
    public FileOutputStream downloadFile(AccountsManager accounts, String remoteFilePath) throws BaseException {
        return null;
    }

    // will create a folder in 'omnidrive' local folder
    public boolean createLocalFolder(String localFolderPath) throws BaseException {
        return true;
    }

}
