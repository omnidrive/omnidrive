package omnidrive.repository;

import java.io.File;

public class Blob extends Object {

    public Blob(File file) {

    }

    @Override
    public Type getType() {
        return Type.BLOB;
    }

    public byte[] getContents() {
        return null;
    }
}
