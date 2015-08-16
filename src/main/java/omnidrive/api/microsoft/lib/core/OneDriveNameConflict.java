package omnidrive.api.microsoft.lib.core;

public enum OneDriveNameConflict {
    Replace("replace"),
    Reaname("rename"),
    Fail("fail");

    private final String text;

    OneDriveNameConflict(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "?nameConflict=" + this.text;
    }
}
