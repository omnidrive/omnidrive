package omnidrive.api.auth;

public enum  AuthSecretKey {
    Dropbox("dropbox"),
    Box("box"),
    GoogleDrive("google"),
    OneDrive("onedrive");

    private final String text;

    AuthSecretKey(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
