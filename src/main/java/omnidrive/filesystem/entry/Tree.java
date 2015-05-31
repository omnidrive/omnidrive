package omnidrive.filesystem.entry;

import java.io.Serializable;
import java.util.List;

public class Tree implements Entry {

    final private List<TreeItem> items;

    public Tree(List<TreeItem> items) {
        this.items = items;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Serializable getMetadata() {
        return null;
    }

}
