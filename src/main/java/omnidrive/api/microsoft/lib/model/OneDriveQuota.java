package omnidrive.api.microsoft.lib.model;

import org.json.JSONObject;

public class OneDriveQuota {
    private long used;
    private long total;
    private long remaining;

    public OneDriveQuota(JSONObject json) {
        this.used = json.getLong("used");
        this.total = json.getLong("total");
        this.remaining = json.getLong("remaining");
    }

    public long getUsed() {
        return used;
    }

    public long getTotal() {
        return total;
    }

    public long getRemaining() {
        return remaining;
    }
}
