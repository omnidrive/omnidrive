package omnidrive.filesystem.entry;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

public class Tree implements Entry {

    final private String id;

    final private File file;

    public Tree(File file) {
        id = UUID.randomUUID().toString();
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return file.getName();
    }

    public Serializable getMetadata() {
        return null;
    }

    public TreeItem[] getItems() {
        File[] files = file.listFiles();
        assert files != null;
        TreeItem[] items = new TreeItem[files.length];

        return items;
    }
}
