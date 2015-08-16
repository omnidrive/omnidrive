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

import java.util.LinkedList;
import java.util.List;

public class OneDriveItem {
    private String id;
    private String name;
    private long size;
    private String downloadUrl;
    private OneDriveParent parent;
    private boolean deleted;
    private OneDriveEntryType type;
    private List<OneDriveChildItem> children = new LinkedList<OneDriveChildItem>();

    public OneDriveItem(JSONObject json) {
        this.id = json.getString("id");
        /*int idEndIndex = this.id.indexOf("!");
        if (idEndIndex >= 0) {
            this.id = this.id.substring(0, idEndIndex);
        }*/

        this.name = json.getString("name");
        this.size = json.getLong("size");
        if (json.has("parentReference")) {
            this.parent = new OneDriveParent(json.getJSONObject("parentReference"));
        } else {
            parent = null;
        }

        this.deleted = json.has("deleted");

        if (json.has("file")) {
            this.type = OneDriveEntryType.File;
            this.downloadUrl = json.getString("@content.downloadUrl");
        } else {
            this.type = OneDriveEntryType.Folder;
            this.downloadUrl = null;
        }

        if (json.has("children")) {
            JSONArray childrenJsonArray = json.getJSONArray("children");
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
