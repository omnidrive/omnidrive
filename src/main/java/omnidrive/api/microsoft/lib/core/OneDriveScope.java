package omnidrive.api.microsoft.lib.core;

public enum OneDriveScope {
    SignIn("wl.signin"), //Allows your application to take advantage of single sign-on capabilities.
    OfflineAccess("wl.offline_access"), //Allows your application to receive a refresh token so it can work offline even when the user isn't active. This scope is not available for token flow.
    ReadOnly("onedrive.readonly"), //Grants read-only permission to all of a user's OneDrive files, including files shared with the user.
    ReadWrite("onedrive.readwrite"), //Grants read and write permission to all of a user's OneDrive files, including files shared with the user. To create sharing links, this scope is required.
    AppFolder("onedrive.appfolder"); //Grants read and write permissions to a specific folder for your application.

    private final String text;

    OneDriveScope(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }

    public static String toString(OneDriveScope... args) {
        String scope = "";
        for (OneDriveScope arg : args) {
            if (scope.isEmpty()) {
                scope += arg.toString();
            } else {
                scope += "%20" + arg.toString();
            }
        }
        return scope;
    }
}
