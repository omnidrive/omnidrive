package omnidrive.api.microsoft.lib.entry;

import org.json.JSONObject;

public class OneDriveChildItem {
    String name;
    String id;
    OneDriveEntryType type;

    public OneDriveChildItem(JSONObject json) {
        this.name = json.getString("name");
        this.id = json.getString("id");
        if (json.get("file") != null) {
            this.type = OneDriveEntryType.File;
        } else {
            this.type = OneDriveEntryType.Folder;
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public OneDriveEntryType getType() {
        return type;
    }
}
