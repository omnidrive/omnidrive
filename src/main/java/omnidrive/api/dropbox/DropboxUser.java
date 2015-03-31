package omnidrive.api.dropbox;

import com.dropbox.core.*;
import omnidrive.api.base.BaseException;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseFolder;
import omnidrive.api.base.BaseUser;

import java.io.*;


public class DropboxUser implements BaseUser {
    private DbxClient client;

    public DropboxUser(DbxRequestConfig config, String accessToken) {
        this.client = new DbxClient(config, accessToken);
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public String getName() {
        String name;

        try {
            name = this.client.getAccountInfo().displayName;
        } catch (DbxException ex) {
            name = null;
        }

        return name;
    }

    public String getId() {
        String id;

        try {
            id = String.valueOf(this.client.getAccountInfo().userId);
        } catch (DbxException ex) {
            id = null;
        }

        return id;
    }

    public BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException {
        File inputFile = new File(localSrcPath);
        BaseFile file = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(inputFile);
            DbxEntry.File uploadedFile = this.client.uploadFile(remoteDestPath, DbxWriteMode.add(), inputFile.length(), inputStream);
            if (uploadedFile != null) {
                file = new DropboxFile(this.client.getMetadata(remoteDestPath), this);
            }
        } catch (FileNotFoundException ex) {
            throw new DropboxException("Input file not found..");
        } catch (DbxException ex) {
            throw new DropboxException("Failed to get file metadata.");
        } catch (IOException ex) {
            throw new DropboxException("Failed to upload file.");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    throw new DropboxException("Failed to close input stream.");
                }
            }
        }

        return file;
    }

    public FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws BaseException {
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(localDestPath);
            this.client.getFile(remoteSrcPath, null, outputStream);
        } catch (IOException ex) {
            throw new DropboxException("Failed to download file.");
        } catch (DbxException ex) {
            throw new DropboxException("Failed to access remote path.");
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                throw new DropboxException("Failed to close output stream.");
            }
        }

        return outputStream;
    }

    public BaseFolder createFolder(String remoteDestPath) throws BaseException {
        BaseFolder folder = null;

        try {
            DbxEntry.Folder dbxFolder = this.client.createFolder(remoteDestPath);
            if (dbxFolder != null) {
                folder = new DropboxFolder(getEntryChildren(remoteDestPath), this);
            }
        } catch (DbxException ex) {
            throw new DropboxException("Failed to create folder.");
        }

        return folder;
    }

    public DropboxFile getFile(String remotePath) throws BaseException {
        DropboxFile dbxFile = null;

        try {
            DbxEntry entry = this.client.getMetadata(remotePath);

            if (entry == null) {
                throw new DropboxException("Failed to get file info.");
            } else if (!entry.isFile()) {
                throw new DropboxException("Entry is not a file.");
            } else {
                dbxFile = new DropboxFile(entry.asFile(), this);
            }
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }

        return dbxFile;
    }

    public BaseFolder getFolder(String path) throws BaseException {
        return new DropboxFolder(getEntryChildren(path), this);
    }

    /*****************************************************************
     * Local methods
     *****************************************************************/

    private DbxEntry.WithChildren getEntryChildren(String path) throws DropboxException {
        DbxEntry.WithChildren rootEntry = null;

        try {
            rootEntry = this.client.getMetadataWithChildren(path);
        } catch (DbxException ex) {
            throw new DropboxException(ex.getMessage());
        }

        return rootEntry;
    }
}
