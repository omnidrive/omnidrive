package omnidrive.api.base;

import java.io.FileOutputStream;

public interface BaseUser {

    public String getName();


    public String getId();


    public BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException;


    public FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws BaseException;


    public BaseFolder createFolder(String remotePath) throws BaseException;


    public BaseFile getFile(String remotePath) throws BaseException;


    public BaseFolder getFolder(String remotePath) throws BaseException;

}
