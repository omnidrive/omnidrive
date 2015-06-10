package omnidrive.filesystem.watcher;

import omnidrive.filesystem.manifest.entry.Blob;

import java.io.File;

public interface Handler {

    void create(Blob blob) throws Exception;

    void create(File file) throws Exception;

    void modify(File file) throws Exception;

    void delete(File file) throws Exception;

}
