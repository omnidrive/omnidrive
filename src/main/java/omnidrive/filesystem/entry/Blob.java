package omnidrive.filesystem.entry;

public class Blob implements Entry {

    private final String id;

    private final long size;

    public Blob(String id, long size) {
        this.id = id;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public long getSize() {
        return size;
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
