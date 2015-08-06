package omnidrive.api.auth;

import javafx.scene.web.WebEngine;
import omnidrive.api.base.Account;
import omnidrive.api.base.AccountException;

public interface Authorizer {

    String authorize();

    void fetchAuthCode(WebEngine engine) throws AccountException;

    void finishAuthProcess(String code) throws AccountException;

    Account createAccount(String accessToken) throws AccountException;

}
