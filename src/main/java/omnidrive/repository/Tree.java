package omnidrive.repository;

import java.util.Iterator;
import java.util.List;

public class Tree extends Object {

    final private List<Object> objects;

    public Tree(List<Object> objects) {
        this.objects = objects;
    }

    @Override
    public Type getType() {
        return Type.TREE;
    }

    public List<Object> getObjects() {
        return objects;
    }

}
