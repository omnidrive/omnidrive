package omnidrive.api.base;

import java.io.File;
import java.io.FileOutputStream;

public interface BaseUser {

    public String getName();


    public String getCountry();


    public String getId();


    public BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException;


    public BaseFile uploadFile(File inputFile, String remoteDestPath) throws BaseException;


    public FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws BaseException;


    public FileOutputStream downloadFile(BaseFile file, String localDestPath) throws BaseException;


    public BaseFolder createFolder(String remoteDestPath) throws BaseException;


    public BaseFile getFile(String remotePath) throws BaseException;


    public BaseFolder getFolder(String path) throws BaseException;

}
