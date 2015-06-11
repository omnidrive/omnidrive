package omnidrive.filesystem.manifest.entry;

import java.io.Serializable;

public class TreeItem implements Serializable {

    final private String id;

    final private String name;

    public TreeItem(String id, String name) {
        this.id = id;
        this.name = name;
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

        return !(id != null ? !id.equals(treeItem.id) : treeItem.id != null) &&
                !(name != null ? !name.equals(treeItem.name) : treeItem.name != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}
