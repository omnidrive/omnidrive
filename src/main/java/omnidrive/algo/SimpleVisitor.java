package omnidrive.algo;

/*
 * Default implementation which does nothing - override only necessary methods
 */
public class SimpleVisitor<T> implements Visitor<T> {

    @Override
    public void preVisit(T node) throws Exception {
        // no-op
    }

    @Override
    public void visit(T node) throws Exception {
        // no-op
    }

    @Override
    public void postVisit(T node) throws Exception {
        // no-op
    }

}
