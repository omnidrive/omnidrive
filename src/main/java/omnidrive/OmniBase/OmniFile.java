package omnidrive.OmniBase;

import java.io.FileOutputStream;
import java.util.Date;

public interface OmniFile {

    public String getName();


    public String getPath();


    public long getSize();


    public Date getLastModified();


    public OmniUser getOwner();

}
