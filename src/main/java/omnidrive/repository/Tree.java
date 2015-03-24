package omnidrive.repository;

import java.util.List;

public class Tree implements Object {

    public static class Entry {

        final private static String SEPARATOR = "\t";

        final public Object.Type type;

        final public Hash hash;

        final public String name;

        public Entry(Object.Type type, Hash hash, String name) {
            this.type = type;
            this.hash = hash;
            this.name = name;
        }

        @Override
        public String toString() {
            return type.toString().toLowerCase() + SEPARATOR +
                    hash.getValue() + SEPARATOR +
                    name;
        }

    }

    final private Hash hash;

    final private String contents;

    public Tree(List<Entry> entries) {
        contents = getContents(entries);
        hash = new Hash(contents);
    }

    private String getContents(List<Entry> entries) {
        StringBuilder sb = new StringBuilder();
        for (Entry entry : entries) {
            sb.append(entry.toString());
        }
        return sb.toString();
    }

    @Override
    public Type getType() {
        return Type.TREE;
    }

    @Override
    public Hash getHash() {
        return hash;
    }

    public String getContents() {
        return contents;
    }

}
