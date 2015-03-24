package omnidrive.repository;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Tree implements Object, Iterable<TreeEntry> {

    final private static char SEPARATOR = '\t';

    final private static char EOL = '\n';

    final private Hash hash;

    final private Iterable<TreeEntry> entries;

    public Tree(Iterable<TreeEntry> entries) {
        this.entries = entries;
        String buffer = getBuffer();
        hash = Hash.of(buffer);
    }

    @Override
    public Type getType() {
        return Type.TREE;
    }

    @Override
    public Hash getHash() {
        return hash;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        String buffer = getBuffer();
        int size = buffer.length();
        out.write(getHeader(size));
        out.write(buffer.getBytes());
        out.close();
    }

    @Override
    public Iterator<TreeEntry> iterator() {
        return entries.iterator();
    }

    private byte[] getHeader(int size) {
        String header = "tree " + size + '\0';
        return header.getBytes();
    }

    private String getBuffer() {
        List<String> transformed = new LinkedList<>();
        for (TreeEntry entry : entries) {
            transformed.add(getLine(entry));
        }
        return StringUtils.join(transformed, EOL);
    }

    private static String getLine(TreeEntry entry) {
        return entry.type.toString().toLowerCase() + SEPARATOR +
                entry.hash.getValue() + SEPARATOR +
                entry.name;
    }

}
