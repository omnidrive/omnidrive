package omnidrive.manifest.entry;

import java.io.Serializable;

public interface Entry extends Serializable {

    enum Type {TREE, BLOB};

    Type getType();

    String getId();

}
