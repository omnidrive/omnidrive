package omnidrive.api.microsoft;

import com.google.gson.Gson;
import com.ning.http.client.Param;
import omnidrive.api.base.BaseException;
import omnidrive.api.base.BaseFile;
import omnidrive.api.base.BaseFolder;
import omnidrive.api.base.BaseUser;
import omnidrive.api.rest_api.RestExecuter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OneDriveUser extends TimerTask implements BaseUser, PropertyChangeListener {

    private static String ApiUrl = "https://apis.live.net/v5.0/";

    private OneDriveAuthProperties properties = new OneDriveAuthProperties();

    private Timer timer = new Timer();

    private Gson gson = new Gson();

    private RestExecuter restApi = new RestExecuter();


    public OneDriveUser(OneDriveAuthProperties properties) {
        this.properties = properties;
        fetchUserInfo();
        watchTokenExpires();
    }

    /*****************************************************************
     * Interface methods
     *****************************************************************/

    public final String getName() throws BaseException {
        return properties.getName();
    }

    public final String getId() throws BaseException {
        return properties.getUserId();
    }

    public final BaseFile uploadFile(String localSrcPath, String remoteDestPath) throws BaseException {
        return null;
    }


    public FileOutputStream downloadFile(String remoteSrcPath, String localDestPath) throws BaseException {
        return null;
    }


    public BaseFolder createFolder(String remotePath) throws BaseException {
        return null;
    }


    public BaseFile getFile(String remotePath) throws BaseException {
        return null;
    }


    public BaseFolder getFolder(String remotePath) throws BaseException {
        return null;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String body = (String)evt.getNewValue();
        if (body != null) {
            if (!updateToken(body)) {
                if (!updateError(body)) {

                }
            }
        }
    }

    // TimerTask
    public void run() {
        refreshToken();
    }

    /*****************************************************************
     * Local methods
     *****************************************************************/

    private void fetchUserInfo() {
        List<Param> params = new ArrayList<Param>();

        params.add(new Param("access_token", this.properties.getAccessToken()));

        this.restApi.executeGet(ApiUrl + "me", params, this);
    }

    private void watchTokenExpires() {
        long delayInMilisec = (this.properties.getExpiresIn() - 60) * 1000; // once in an ~hour (-1min)
        this.timer.schedule(this, delayInMilisec);
    }

    private void refreshToken() {
        OneDriveApi.refreshToken(this.properties.getRefreshToken(), this);
    }

    private boolean updateToken(String body) {
        boolean gotToken = false;

        OneDriveAuthProperties properties = gson.fromJson(body, OneDriveAuthProperties.class);
        if (properties.getAccessToken() != null) {
            this.properties = properties;
            gotToken = true;
        }

        return gotToken;
    }

    private boolean updateError(String body) {
        boolean gotError = false;

        OneDriveAuthError error = gson.fromJson(body, OneDriveAuthError.class);
        if (error.getError() != null) {
            this.properties.addError(error);
            gotError = true;
        }

        return gotError;
    }
}
