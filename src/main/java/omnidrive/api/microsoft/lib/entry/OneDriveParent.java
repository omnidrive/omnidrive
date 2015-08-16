package omnidrive.api.microsoft.lib.entry;

import org.json.JSONObject;

public class OneDriveParent {

    private String driveId;
    private String id;
    private String path;
    private String name;

    public OneDriveParent(JSONObject json) {
        this.driveId = json.getString("driveId");
        this.id = json.getString("id");
        this.path = json.getString("path").substring("/drive/root:".length());
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
