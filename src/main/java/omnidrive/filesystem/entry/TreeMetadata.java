package omnidrive.filesystem.entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeMetadata implements Serializable {

    final public List<TreeItem> items = new ArrayList<TreeItem>();

    public TreeMetadata(TreeItem... items) {
        Collections.addAll(this.items, items);
    }


}
