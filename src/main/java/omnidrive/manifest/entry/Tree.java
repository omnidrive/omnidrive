package omnidrive.manifest.entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tree implements Entry {

    final private String id;

    final private List<TreeItem> items;

    public Tree(String id) {
        this(id, new ArrayList<TreeItem>());
    }

    public Tree(String id, List<TreeItem> items) {
        this.id = id;
        this.items = items;
    }

    public Type getType() {
        return Type.TREE;
    }

    public String getId() {
        return id;
    }

    public List<TreeItem> getItems() {
        return items;
    }

    public TreeItem getItem(String name) {
        for (TreeItem item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public void addItem(TreeItem item) {
        items.add(item);
    }

    public void removeItem(String id) {
        Iterator<TreeItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            TreeItem item = iterator.next();
            if (item.getId().equals(id)) {
                iterator.remove();
                break;
            }
        }
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
        int result = id.hashCode();
        result = 31 * result + items.hashCode();
        return result;
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
