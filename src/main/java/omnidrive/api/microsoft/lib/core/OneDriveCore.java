package omnidrive.api.microsoft.lib.core;

import omnidrive.api.microsoft.lib.auth.OneDriveOAuth;
import omnidrive.api.microsoft.lib.entry.OneDriveEntryType;
import omnidrive.api.microsoft.lib.entry.OneDriveItem;
import omnidrive.api.microsoft.lib.model.OneDriveOwner;
import omnidrive.api.microsoft.lib.model.OneDriveQuota;
import omnidrive.api.microsoft.lib.rest.OneDriveRestApi;
import omnidrive.api.microsoft.lib.rest.RestApi;
import omnidrive.api.microsoft.lib.rest.RestApiErrorListener;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;

public class OneDriveCore implements RestApiErrorListener {

    private final OneDriveRestApi restApi;

    private boolean refreshAuthSucceed;
    private OneDriveOAuth oauth;

    public OneDriveCore(OneDriveOAuth oauth) {
        this.oauth = oauth;
        this.restApi = new OneDriveRestApi(this.oauth.getAccessToken());
        this.restApi.resgiterToErrors(this);
    }

    public static OneDriveCore authorize(String clientId, String clientSecret, String code) throws Exception {
        RestApi restApi = new RestApi();

        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", OneDriveRestApi.ONEDRIVE_API_REDIRECT_URL);

        JSONObject result = restApi.doPost(
                OneDriveRestApi.ONEDRIVE_API_AUTH_TOKEN_URL,
                null,
                null,
                params
        );

        OneDriveOAuth oauth = new OneDriveOAuth(clientId, clientSecret, result);
        OneDriveCore core = new OneDriveCore(oauth);

        return core;
    }

    public void refreshAuthorization() throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", this.oauth.getClientId());
        params.put("client_secret", this.oauth.getClientSecret());
        params.put("refresh_token", this.oauth.getRefreshToken());
        params.put("grant_type", "refresh_token");
        params.put("redirect_uri", OneDriveRestApi.ONEDRIVE_API_REDIRECT_URL);

        JSONObject result = restApi.doPost(
                OneDriveRestApi.ONEDRIVE_API_AUTH_TOKEN_URL,
                null,
                null,
                params
        );

        this.oauth = new OneDriveOAuth(this.oauth.getClientId(), this.oauth.getClientSecret(), result);
    }

    public void logout() throws Exception {
        String query =
                "?client_id=" + this.oauth.getClientId() +
                "&redirect_uri=" + OneDriveRestApi.ONEDRIVE_API_REDIRECT_URL;

        restApi.doGet(
                OneDriveRestApi.ONEDRIVE_API_LOGOUT_URL,
                null,
                query
        );
    }

    public OneDriveOwner getOwner() throws Exception {
        refreshTokenIfNeeded();

        String path = OneDriveRestApi.ONEDRIVE_API_DRIVE;

        JSONObject result = restApi.doGet(
                OneDriveRestApi.ONEDRIVE_API_URL,
                path,
                null
        );

        return new OneDriveOwner(result.getJSONObject("owner"));
    }

    public OneDriveItem getRootItem() throws Exception {
        return getItemById("root", true);
    }

    public OneDriveItem getItemById(String itemId, boolean withChildren) throws Exception {
        refreshTokenIfNeeded();

        String path =
                OneDriveRestApi.ONEDRIVE_API_DRIVE +
                OneDriveRestApi.ONEDRIVE_API_ITEM_BY_ID +
                "/" + itemId;


        JSONObject result = restApi.doGet(
                OneDriveRestApi.ONEDRIVE_API_URL,
                path,
                withChildren ? OneDriveRestApi.ONEDRIVE_API_EXPAND_CHILDREN_QUERY : null
        );

        return new OneDriveItem(result);
    }

    public OneDriveItem getItemByPath(String itemPath, boolean withChildren) throws Exception {
        refreshTokenIfNeeded();

        if (itemPath.startsWith("/")) {
            itemPath = itemPath.substring(1);
        }

        String path =
                OneDriveRestApi.ONEDRIVE_API_DRIVE +
                OneDriveRestApi.ONEDRIVE_API_ITEM_BY_PATH +
                "/" + itemPath;

        JSONObject result = restApi.doGet(
                OneDriveRestApi.ONEDRIVE_API_URL,
                path,
                withChildren ? OneDriveRestApi.ONEDRIVE_API_EXPAND_CHILDREN_QUERY : null
        );

        return new OneDriveItem(result);
    }

    public String uploadItem(String parentId, String name, InputStream inputStream) throws Exception {
        refreshTokenIfNeeded();

        String path =
                OneDriveRestApi.ONEDRIVE_API_DRIVE +
                OneDriveRestApi.ONEDRIVE_API_ITEM_BY_ID +
                "/" + parentId +
                OneDriveRestApi.ONEDRIVE_API_CHILDREN +
                "/" + name +
                OneDriveRestApi.ONEDRIVE_API_CONTENT;

        JSONObject result = restApi.doPut(
                OneDriveRestApi.ONEDRIVE_API_URL,
                path,
                null,
                inputStream
        );

        if (result.getString("id") == null || result.getString("id").isEmpty()) {
            throw new Exception("OneDriveCore: failed to upload item.");
        }

        return result.getString("id");
    }

    public OneDriveItem updateItem(String itemId, InputStream inputStream, OneDriveNameConflict nameConflict) throws Exception {
        refreshTokenIfNeeded();

        String path =
                OneDriveRestApi.ONEDRIVE_API_DRIVE +
                OneDriveRestApi.ONEDRIVE_API_ITEM_BY_ID +
                "/" + itemId +
                OneDriveRestApi.ONEDRIVE_API_CONTENT;

        JSONObject result = restApi.doPut(
                OneDriveRestApi.ONEDRIVE_API_URL,
                path,
                nameConflict.toQuery(),
                inputStream
        );

        if (result.getString("id") == null || result.getString("id").isEmpty() || !result.getString("id").equals(itemId)) {
            throw new Exception("OneDriveCore: failed to update item.");
        }

        return new OneDriveItem(result);
    }

    public void deleteItem(String itemId) throws Exception {
        refreshTokenIfNeeded();

        String path =
                OneDriveRestApi.ONEDRIVE_API_DRIVE +
                OneDriveRestApi.ONEDRIVE_API_ITEM_BY_ID +
                "/" + itemId;

        JSONObject result = restApi.doDelete(
                OneDriveRestApi.ONEDRIVE_API_URL,
                path,
                null
        );
    }

    public long downloadItem(String itemId, OutputStream outputStream) throws Exception {
        refreshTokenIfNeeded();

        OneDriveItem item = getItemById(itemId, false);
        if (item.getType() != OneDriveEntryType.File) {
            throw new Exception("OneDriveCore: item is not a file");
        }

        URL downloadUrl = new URL(item.getDownloadUrl());
        ReadableByteChannel readChannel = Channels.newChannel(downloadUrl.openStream());
        WritableByteChannel writeChannel = Channels.newChannel(outputStream);
        return ChannelTools.fastChannelCopy(readChannel, writeChannel);
    }

    public String createFolderItem(String name, OneDriveNameConflict nameConflict) throws Exception {
        refreshTokenIfNeeded();

        String path =
                OneDriveRestApi.ONEDRIVE_API_DRIVE +
                OneDriveRestApi.ONEDRIVE_API_ROOT +
                OneDriveRestApi.ONEDRIVE_API_CHILDREN;

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("name", name);
        jsonBody.put("folder", new JSONObject("{}"));

        JSONObject result = restApi.doPost(
                OneDriveRestApi.ONEDRIVE_API_URL,
                path,
                nameConflict.toQuery(),
                jsonBody
        );

        return result.getString("id");
    }

    public OneDriveQuota getQuota() throws Exception {
        refreshTokenIfNeeded();

        JSONObject json = restApi.doGet(
                OneDriveRestApi.ONEDRIVE_API_URL,
                OneDriveRestApi.ONEDRIVE_API_DRIVE,
                null
        );

        return new OneDriveQuota(json.getJSONObject("quota"));
    }

    public OneDriveOAuth getOauth() {
        return oauth;
    }

    private void refreshTokenIfNeeded() throws Exception {
        if (this.oauth.hasExpired()) {
            refreshAuthorization();
        }
    }

    @Override
    public void errorOccured(int code) {
        if (code == 401) {
            try {
                refreshAuthorization();
                if (!oauth.hasExpired()) {
                    refreshAuthSucceed = true;
                }
            } catch (Exception ex) {
                refreshAuthSucceed = false;
            }
        }
    }

    @Override
    public boolean shouldContinueOnError(int code) {
        if (code == 401 && refreshAuthSucceed) {
            refreshAuthSucceed = false;
            return true;
        }

        return false;
    }

    private static class ChannelTools {
        public static long fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
            long size = 0;

            final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
            while (src.read(buffer) != -1) {
                // prepare the buffer to be drained
                buffer.flip();
                // write to the channel, may block
                size += dest.write(buffer);
                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buffer.compact();
            }
            // EOF will leave buffer in fill state
            buffer.flip();
            // make sure the buffer is fully drained.
            while (buffer.hasRemaining()) {
                size += dest.write(buffer);
            }

            return size;
        }
    }
}
