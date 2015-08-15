package omnidrive.manifest.entry;

import omnidrive.api.base.AccountType;

public class Blob implements Entry {

    private final String id;

    private final long size;

    private final AccountType account;

    public Blob(String id, long size, AccountType account) {
        this.id = id;
        this.size = size;
        this.account = account;
    }

    public Type getType() {
        return Type.BLOB;
    }

    public String getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public AccountType getAccount() {
        return account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Blob blob = (Blob) o;

        return size == blob.size &&
                id.equals(blob.id) &&
                account == blob.account;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + account.hashCode();
        return result;
    }

}
