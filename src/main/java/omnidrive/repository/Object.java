package omnidrive.repository;

public interface Object {

    enum Type {TREE, BLOB}

    Type getType();

    Hash getHash();

}
