package omnidrive.repository;

public class TreeEntry {

    final public Object.Type type;

    final public Hash hash;

    final public String name;

    public TreeEntry(Object.Type type, Hash hash, String name) {
        this.type = type;
        this.hash = hash;
        this.name = name;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeEntry entry = (TreeEntry) o;

        return type == entry.type &&
                !(hash != null ? !hash.equals(entry.hash) : entry.hash != null) &&
                !(name != null ? !name.equals(entry.name) : entry.name != null);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}
