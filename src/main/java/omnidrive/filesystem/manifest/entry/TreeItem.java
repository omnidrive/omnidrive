package omnidrive.filesystem.manifest.entry;

import java.io.Serializable;

public class TreeItem implements Serializable {

    final private Entry.Type type;

    final private String id;

    final private String name;

    final private long lastModified;

    public TreeItem(Entry.Type type, String id, String name, long lastModified) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.lastModified = lastModified;
    }

    public Entry.Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getLastModified() {
        return lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeItem treeItem = (TreeItem) o;

        return type == treeItem.type &&
                id.equals(treeItem.id) &&
                name.equals(treeItem.name) &&
                lastModified == treeItem.lastModified;

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (int) (lastModified ^ (lastModified >>> 32));
        return result;
    }

}
