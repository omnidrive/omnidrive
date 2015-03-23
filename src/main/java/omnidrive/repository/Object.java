package omnidrive.repository;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public abstract class Object {

    public enum Type {TREE, BLOB}

    public abstract Type getType();

    public String getHash(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            final byte[] buffer = new byte[1024];
            for (int read; (read = is.read(buffer)) != -1;) {
                md.update(buffer, 0, read);
            }

            Formatter formatter = new Formatter();
            for (final byte b : md.digest()) {
                formatter.format("%02x", b);
            }

            return formatter.toString();

        } catch (Exception e) {
            return null;
        }
    }

}
