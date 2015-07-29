package omnidrive.algo;

public interface Visitor<T> {

    void preVisit(T node) throws Exception;

    void visit(T node) throws Exception;

    void postVisit(T node) throws Exception;

}
