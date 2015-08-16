package omnidrive.api.microsoft.lib.entry;

import org.json.JSONObject;

public class OneDriveChildItem {
    private String name;
    private String id;
    private OneDriveEntryType type;

    public OneDriveChildItem(JSONObject json) {
        this.name = json.getString("name");

        this.id = json.getString("id");
        /*int index = this.id.indexOf("!");
        if (index >= 0) {
            this.id = this.id.substring(0, index);
        }*/

        if (json.has("file")) {
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
