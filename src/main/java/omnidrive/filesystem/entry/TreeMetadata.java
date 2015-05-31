package omnidrive.filesystem.entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeMetadata implements Serializable {

    public class Item {

        public String id;

        public String name;

    }

    public List<Item> items = new ArrayList<>();

}
