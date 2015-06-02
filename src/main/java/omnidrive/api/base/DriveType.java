package omnidrive.api.base;

public enum DriveType {
    Dropbox("Dropbox"),
    GoogleDrive("Google Drive"),
    Box("Box");

    private final String text;

    private DriveType(String text) {
        this.text = text;
    }

    public static DriveType getType(int index) {
        return DriveType.values()[index];
    }

    public static int length() {
        return DriveType.values().length;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
