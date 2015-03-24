package omnidrive.repository;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class Blob implements Object {

    final private File file;

    final private Hash hash;

    public Blob(File file) throws IOException {
        this.file = file;
        hash = Hash.of(file);
    }

    @Override
    public Type getType() {
        return Type.BLOB;
    }

    @Override
    public Hash getHash() {
        return hash;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        out.write(getHeader());
        Files.copy(file.toPath(), out);
        out.close();
    }

    private byte[] getHeader() {
        String header = "blob " + file.length() + '\0';
        return header.getBytes();
    }

}
