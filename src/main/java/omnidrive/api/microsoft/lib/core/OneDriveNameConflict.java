package omnidrive.api.microsoft.lib.core;

public enum OneDriveNameConflict {
    Replace("replace"),
    Reaname("rename"),
    Fail("fail");

    private final String text;

    OneDriveNameConflict(String text) {
        this.text = text;
    }

    public String toQuery() {
        return "?nameConflict=" + this.text;
    }
}
