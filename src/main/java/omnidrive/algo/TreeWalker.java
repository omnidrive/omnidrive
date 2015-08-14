package omnidrive.algo;

public class TreeWalker<T extends TreeNode<T>> {

    final private Visitor<T> visitor;

    public TreeWalker(Visitor<T> visitor) {
        this.visitor = visitor;
    }

    public void walk(T node) throws Exception {
        visitor.visit(node);
        for (T child : node.getChildren().values()) {
            visitor.preVisit(child);
            walk(child);
            visitor.postVisit(child);
        }
    }

}
