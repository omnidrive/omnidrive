package omnidrive.api.base;

public enum DriveType {
    Dropbox("Dropbox"),
    GoogleDrive("Google Drive"),
    Box("Box");

    private final String text;

    private DriveType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
