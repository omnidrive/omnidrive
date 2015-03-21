package omnidrive.Dropbox;

import com.dropbox.core.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by assafey on 3/21/15.
 */
public class DropboxUser {
    private DbxClient client;

    public DropboxUser(DbxRequestConfig config, String accessToken) {
        fetchUserInfo(config, accessToken);
    }

    public String getName() {
        String name = null;

        try {
            name = this.client.getAccountInfo().displayName;
        } catch (DbxException ex) {

        }

        return name;
    }

    public String getCountry() {
        String country = null;

        try {
            country = this.client.getAccountInfo().country;
        } catch (DbxException ex) {

        }

        return country;
    }

    public String getId() {
        String id = null;

        try {
            id = String.valueOf(this.client.getAccountInfo().userId);
        } catch (DbxException ex) {

        }

        return id;
    }

    public DropboxFile uploadFile(DropboxFolder folder, FileInputStream file, String filename) throws DropboxException {
        DropboxFile newFile = null;
        File inputFile = new File(filename);
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(inputFile);
            String path = folder.getPath() + "/" + filename;
            DbxEntry.File dbxFile = this.client.uploadFile(path, DbxWriteMode.add(), inputFile.length(), inputStream);

            newFile = new DropboxFile(dbxFile, this);
        } catch (FileNotFoundException ex) {
            throw new DropboxException(ex.getMessage());
        } catch (IOException ex) {
            throw new DropboxException(ex.getMessage());
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex){
                    throw new DropboxException(ex.getMessage());
                }
            }
        }

        return newFile;
    }

    public FileOutputStream downloadFile(DropboxFile file) throws DropboxException {
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file.getName());
            this.client.getFile(file.getPath(), null, outputStream);
        } catch (FileNotFoundException ex) {
            throw new DropboxException(ex.getMessage());
        } catch (IOException ex) {
            throw new DropboxException(ex.getMessage());
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }

        return outputStream;
    }

    public List<DropboxFolder> getFolders() {
        List<DropboxFolder> folders = new ArrayList<DropboxFolder>();

        // TODO - get user's folders list

        return folders;
    }

    private void fetchUserInfo(DbxRequestConfig config, String accessToken) {
        this.client = new DbxClient(config, accessToken);
    }
}
