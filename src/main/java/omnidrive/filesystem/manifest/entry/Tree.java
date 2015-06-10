package omnidrive.filesystem.manifest.entry;

import java.util.Collections;
import java.util.List;

public class Tree implements Entry {

    final private String id;

    final private List<TreeItem> items;

    public Tree(String id) {
        this.id = id;
        this.items = Collections.emptyList();
    }

    public Tree(String id, List<TreeItem> items) {
        this.id = id;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public List<TreeItem> getItems() {
        return items;
    }
}
