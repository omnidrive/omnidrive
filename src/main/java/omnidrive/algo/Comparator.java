package omnidrive.algo;

public interface Comparator<L, R> {
    boolean areEqual(L left, R right);
}
