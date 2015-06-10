package omnidrive.filesystem.entry;

public class Blob implements Entry {

    private final String id;

    private final long size;

    private final String account;

    public Blob(String id, long size, String account) {
        this.id = id;
        this.size = size;
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public String getAccount() {
        return account;
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
