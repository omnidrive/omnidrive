package omnidrive.api.base;

import java.io.FileOutputStream;

public interface BaseUser {

    BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException;


    FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws BaseException;


    BaseFolder createFolder(String remotePath) throws BaseException;


    BaseFile getFile(String remotePath) throws BaseException;


    BaseFolder getFolder(String remotePath) throws BaseException;

}
