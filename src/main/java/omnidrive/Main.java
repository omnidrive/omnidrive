package omnidrive;

import omnidrive.OmniBase.*;
import omnidrive.Dropbox.*;

import java.util.List;

public class Main {

    public static void main(String[] args)
    {
        OmniApi dbxApi = new DropboxApi();

        try {

            OmniUser dbxUser = dbxApi.login();

            OmniFolder rootFolder = dbxUser.getFolder("/"); // get root directory

            List<OmniFolder> rootFolders = rootFolder.getFolders();

            System.out.println("Root Folder:");
            for (OmniFolder folder : rootFolders) {
                System.out.println(folder.getName());
            }

        } catch (OmniException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

}
