package omnidrive.filesystem.entry;

import java.io.*;
import java.util.UUID;

public class Blob implements Entry {

    private final String id;

    private final InputStream inputStream;

    private final long size;

    public Blob(File file) throws FileNotFoundException {
        id = UUID.randomUUID().toString();
        inputStream = new FileInputStream(file);
        size = file.length();
    }

    public Blob(String id, InputStream inputStream, long size) {
        this.id = id;
        this.inputStream = inputStream;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public long getSize() {
        return size;
    }

    public Blob copyWithNewId(String newId) {
        return new Blob(newId, inputStream, size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blob blob = (Blob) o;

        return id.equals(blob.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
