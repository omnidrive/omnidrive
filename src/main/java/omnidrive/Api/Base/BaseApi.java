package omnidrive.api.base;

import javafx.scene.web.WebEngine;
import omnidrive.api.managers.LoginManager;
import org.w3c.dom.Document;

public interface BaseApi {

    public void login(LoginManager loginManager) throws BaseException;

    public void authCompleted(WebEngine engine);

    public String getName();

}
