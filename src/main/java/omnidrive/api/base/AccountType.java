package omnidrive.api.base;

public enum AccountType {
    Dropbox("Dropbox"),
    GoogleDrive("Google Drive"),
    Box("Box");

    private final String text;

    AccountType(String text) {
        this.text = text;
    }

    public static AccountType getType(int index) {
        return AccountType.values()[index];
    }

    public static int length() {
        return AccountType.values().length;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
