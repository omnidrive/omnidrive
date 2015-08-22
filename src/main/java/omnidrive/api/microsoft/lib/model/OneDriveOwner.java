package omnidrive.api.microsoft.lib.model;

import org.json.JSONObject;

public class OneDriveOwner {
    String id;
    String name;

    public OneDriveOwner(JSONObject json) {
        JSONObject jsonUser = json.getJSONObject("user");
        this.id = jsonUser.getString("id");
        this.name = jsonUser.getString("displayName");
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
