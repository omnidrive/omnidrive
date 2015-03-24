package omnidrive.repository;

import java.io.File;

public class Blob implements Object {

    public Blob(File file) {

    }

    @Override
    public Type getType() {
        return Type.BLOB;
    }

    @Override
    public Hash getHash() {
        return null;
    }

    public byte[] getContents() {
        return null;
    }
}
