package omnidrive.OmniBase;

import java.util.List;

public interface OmniFolder {

    public String getName();


    public String getPath();


    public OmniUser getOwner();


    public List<OmniFolder> getFolders();


    public List<OmniFile> getFiles();

}
