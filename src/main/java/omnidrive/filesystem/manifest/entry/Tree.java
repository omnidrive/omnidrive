package omnidrive.filesystem.manifest.entry;

import java.util.ArrayList;
import java.util.List;

public class Tree implements Entry {

    final private String id;

    final private List<TreeItem> items;

    public Tree(String id) {
        this.id = id;
        this.items = new ArrayList<TreeItem>();
    }

    public Type getType() {
        return Type.TREE;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tree tree = (Tree) o;

        return id.equals(tree.id) && listsEqual(items, tree.items);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private boolean listsEqual(List<TreeItem> list1, List<TreeItem> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            TreeItem item1 = list1.get(i);
            TreeItem item2 = list2.get(i);
            if (!item1.equals(item2)) {
                return false;
            }
        }
        return true;
    }

}
