package omnidrive.api.base;

import javafx.scene.web.WebEngine;
import omnidrive.api.managers.LoginManager;

public interface BaseApi {

    public void login(LoginManager loginManager) throws BaseException;

    public void authCompleted(WebEngine engine);

    public String getName();

}
