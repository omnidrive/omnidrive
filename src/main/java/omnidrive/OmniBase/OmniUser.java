package omnidrive.OmniBase;

import java.io.FileOutputStream;

public interface OmniUser {

    public String getName();


    public String getCountry();


    public String getId();


    public OmniFile uploadFile(String localSrcPath, String remoteDestPath) throws OmniException;


    public FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws OmniException;


    public OmniFolder createFolder(String remoteDestPath) throws OmniException;


    public OmniFile getFile(String remotePath) throws OmniException;


    public OmniFolder getFolder(String path) throws OmniException;

}
