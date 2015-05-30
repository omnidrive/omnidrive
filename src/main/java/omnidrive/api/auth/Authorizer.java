package omnidrive.api.auth;

import javafx.scene.web.WebEngine;
import omnidrive.api.base.BaseException;

public interface Authorizer {

    String authorize();

    void fetchAuthCode(WebEngine engine) throws BaseException;

    void finishAuthProcess(String code) throws BaseException;


}
