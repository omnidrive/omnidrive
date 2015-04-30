package omnidrive.api.managers;

import omnidrive.api.base.BaseUser;
import omnidrive.api.base.DriveType;

public class AccountsManager {

    private static AccountsManager manager = null;

    private final BaseUser[] loggedInUsers = new BaseUser[DriveType.values().length];

    // singleton
    private AccountsManager() {

    }

    public static AccountsManager getAccountsManager() {
        if (manager == null) {
            manager = new AccountsManager();
        }

        return manager;
    }

    public void setLoggedInUser(DriveType type, BaseUser user) {
        this.loggedInUsers[type.ordinal()] = user;
    }

    public BaseUser getLoggedInUser(DriveType type) {
        return this.loggedInUsers[type.ordinal()];
    }
}
