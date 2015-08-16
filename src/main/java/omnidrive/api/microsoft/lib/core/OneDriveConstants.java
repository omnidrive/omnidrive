package omnidrive.api.microsoft.lib.core;

public class OneDriveConstants {

    public static final String ONEDRIVE_API_AUTH_URL = "https://login.live.com/oauth20_authorize.srf";
    public static final String ONEDRIVE_API_REDIRECT_URL = "https://login.live.com/oauth20_desktop.srf";
    public static final String ONEDRIVE_API_AUTH_TOKEN_URL = "https://login.live.com/oauth20_token.srf";

    public static final String ONEDRIVE_API_VERSION = "/v1.0";
    public static final String ONEDRIVE_API_URL = "https://api.onedrive.com" + ONEDRIVE_API_VERSION;

    public static final String ONEDRIVE_API_DRIVE = "/drive";
    public static final String ONEDRIVE_API_ITEM_BY_PATH = "/root:";
    public static final String ONEDRIVE_API_ITEM_BY_ID = "/items";
    public static final String ONEDRIVE_API_ROOT = "/root";
    public static final String ONEDRIVE_API_CONTENT = "/content";
    public static final String ONEDRIVE_API_CHILDREN = "/children";

    public static final String ONEDRIVE_API_EXPAND_CHILDREN = "?expand=children";
}
