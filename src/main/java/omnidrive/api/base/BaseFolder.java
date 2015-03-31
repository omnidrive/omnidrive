package omnidrive.Api.Base;

import java.util.List;

public interface BaseFolder {

    public String getName();


    public String getPath();


    public BaseUser getOwner();


    public List<BaseFolder> getFolders();


    public List<BaseFile> getFiles();

}
