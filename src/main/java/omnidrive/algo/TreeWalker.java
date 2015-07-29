package omnidrive.algo;

public class TreeWalker<T extends TreeNode<T>> {

    public void walk(T node, Visitor<T> visitor) throws Exception {
        visitor.visit(node);
        for (T child : node.getChildren().values()) {
            visitor.preVisit(child);
            walk(child, visitor);
            visitor.postVisit(child);
        }
    }

}
