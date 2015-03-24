package omnidrive.repository;

import java.io.IOException;
import java.io.OutputStream;

public interface Object {

    enum Type {TREE, BLOB}

    Type getType();

    Hash getHash();

    void write(OutputStream out) throws IOException;

}
