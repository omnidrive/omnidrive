package omnidrive.api.managers;


import omnidrive.api.base.BaseUser;
import omnidrive.api.base.DriveType;

import java.util.ArrayList;
import java.util.List;

public class AccountsManager {

    private static AccountsManager manager = null;

    private final List<BaseUser> loggedInUsers;

    // singleton
    private AccountsManager() {
        this.loggedInUsers = new ArrayList<BaseUser>(DriveType.values().length);
    }

    public static AccountsManager getAccountsManager() {
        if (manager == null) {
            manager = new AccountsManager();
        }

        return manager;
    }

    public void setLoggedInUser(DriveType type, BaseUser user) {
        this.loggedInUsers.add(type.ordinal(), user);
    }

    public BaseUser getLoggedInUser(DriveType type) {
        return this.loggedInUsers.get(type.ordinal());
    }
}
