package omnidrive.repository;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Hash {

    private static MessageDigest md = null;

    private String value;

    static {
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException ignored) {}
        assert md != null;
    }

    public Hash(String raw) {
        md.reset();
        md.update(raw.getBytes());
        setValue();
    }

    public Hash(File file) throws IOException {
        md.reset();
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[1024];
        for (int read; (read = is.read(buffer)) != -1;) {
            md.update(buffer, 0, read);
        }
        setValue();
    }

    public String getValue() {
        return value;
    }

    private void setValue() {
        Formatter formatter = new Formatter();
        formatter.flush();
        for (byte b : md.digest()) {
            formatter.format("%02x", b);
        }
        value = formatter.toString();
        formatter.close();
    }

}
