package omnidrive.filesystem.manifest.entry;

import java.io.Serializable;

public class TreeItem implements Serializable {

    final private Entry.Type type;

    final private String id;

    final private String name;

    public TreeItem(Entry.Type type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeItem treeItem = (TreeItem) o;

        return type == treeItem.type &&
                id.equals(treeItem.id) &&
                name.equals(treeItem.name);

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

}
