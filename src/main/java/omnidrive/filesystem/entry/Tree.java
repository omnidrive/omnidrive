package omnidrive.filesystem.entry;

import java.util.Collections;
import java.util.List;

public class Tree implements Entry {

    final private String id;

    final private List<TreeItem> items = Collections.emptyList();

    public Tree(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<TreeItem> getItems() {
        return items;
    }
}
