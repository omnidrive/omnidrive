package omnidrive.filesystem.entry;

import java.io.Serializable;

public class BlobMetadata implements Serializable {

    public long size;

    public String account;

    public BlobMetadata(long size, String account) {
        this.size = size;
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlobMetadata metadata = (BlobMetadata) o;

        if (size != metadata.size) return false;
        return account.equals(metadata.account);

    }

    @Override
    public int hashCode() {
        int result = (int) (size ^ (size >>> 32));
        result = 31 * result + account.hashCode();
        return result;
    }

}
