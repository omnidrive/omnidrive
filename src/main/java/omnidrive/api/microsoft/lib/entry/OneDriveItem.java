package omnidrive.api.microsoft.lib.entry;

/*
{
  "id": "string (identifier)",
  "name": "string",
  "eTag": "string (etag)",
  "cTag": "string (etag)",
  "createdBy": { "@odata.type": "oneDrive.identitySet" },
  "createdDateTime": "string (timestamp)",
  "lastModifiedBy": { "@odata.type": "oneDrive.identitySet" },
  "lastModifiedDateTime": "string (timestamp)",
  "size": 1024,
  "webUrl": "url",
  "description": "string",
  "parentReference": { "@odata.type": "oneDrive.itemReference"},
  "children": [ { "@odata.type": "oneDrive.item" } ],
  "folder": { "@odata.type": "oneDrive.folder" },
  "file": { "@odata.type": "oneDrive.file" },
  "fileSystemInfo": {"@odata.type": "oneDrive.fileSystemInfo"},
  "image": { "@odata.type": "oneDrive.image" },
  "photo": { "@odata.type": "oneDrive.photo" },
  "audio": { "@odata.type": "oneDrive.audio" },
  "video": { "@odata.type": "oneDrive.video" },
  "location": { "@odata.type": "oneDrive.location" },
  "deleted": { "@odata.type": "oneDrive.deleted"},
  "specialFolder": { "@odata.type": "oneDrive.specialFolder" },
  "thumbnails": [ {"@odata.type": "oneDrive.thumbnailSet"} ],
  "@name.conflictBehavior": "string",
  "@content.downloadUrl": "url",
  "@content.sourceUrl": "url"
}
 */

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class OneDriveItem {
    private final String id;
    private final String name;
    private final long size;
    private final String description;
    private final String downloadUrl;
    private final OneDriveParent parent;
    private final boolean deleted;
    private final OneDriveEntryType type;
    private final List<OneDriveChildItem> children = new LinkedList<OneDriveChildItem>();

    public OneDriveItem(JSONObject json) {
        this.id = json.getString("id");
        this.name = json.getString("name");
        this.size = json.getLong("size");
        this.description = json.getString("description");
        this.parent = new OneDriveParent(json.getJSONObject("parentReference"));
        this.deleted = json.getJSONObject("deleted").length() != 0;
        this.downloadUrl = json.getString("@content.downloadUrl");

        if (json.get("file") != null) {
            this.type = OneDriveEntryType.File;
        } else {
            this.type = OneDriveEntryType.Folder;
        }

        JSONArray childrenJsonArray = json.getJSONArray("children");
        if (childrenJsonArray != null) {
            for (int index = 0; index < childrenJsonArray.length(); index++) {
                JSONObject childJson = childrenJsonArray.getJSONObject(index);
                this.children.add(new OneDriveChildItem(childJson));
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public OneDriveParent getParent() {
        return parent;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public OneDriveEntryType getType() {
        return type;
    }

    public List<OneDriveChildItem> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }
}
