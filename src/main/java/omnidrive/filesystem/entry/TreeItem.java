package omnidrive.filesystem.entry;

public class TreeItem {

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

}
