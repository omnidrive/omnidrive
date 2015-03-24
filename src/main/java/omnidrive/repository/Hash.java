package omnidrive.repository;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Hash {

    final private static String ALGORITHM = "SHA1";

    private static MessageDigest md = null;

    final private String value;

    static {
        try {
            md = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {}
    }

    public Hash(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hash hash = (Hash) o;
        return value.equals(hash.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public static Hash of(String value) {
        synchronized (md) {
            md.reset();
            md.update(value.getBytes());
            return new Hash(format());
        }
    }

    public static Hash of(File file) throws IOException {
        synchronized (md) {
            md.reset();
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            for (int read; (read = is.read(buffer)) != -1;) {
                md.update(buffer, 0, read);
            }
            return new Hash(format());
        }
    }

    private static String format() {
        assert md != null;

        Formatter formatter = new Formatter();
        formatter.flush();
        for (byte b : md.digest()) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();

        return result;
    }

}
