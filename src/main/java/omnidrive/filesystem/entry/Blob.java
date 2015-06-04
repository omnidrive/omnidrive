package omnidrive.filesystem.entry;

import java.io.*;
import java.util.UUID;

public class Blob implements Entry {

    private String id;

    private final File file;

    public Blob(File file) {
        id = UUID.randomUUID().toString();
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return file.getName();
    }

    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public long getSize() {
        return file.length();
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
