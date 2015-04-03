package omnidrive.api.base;

import javafx.scene.web.WebEngine;

public interface Authorizer {

    String authorize();

    void fetchAuthCode(WebEngine engine) throws BaseException;

    void finishAuthProcess(String code) throws BaseException;
}
