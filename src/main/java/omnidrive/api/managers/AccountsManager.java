package omnidrive.api.managers;

import omnidrive.api.base.BaseUser;
import omnidrive.api.base.DriveType;

import java.util.LinkedList;
import java.util.List;

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

    public void removeLoggedInUser(DriveType type) {
        if (this.loggedInUsers[type.ordinal()] != null) {
            this.loggedInUsers[type.ordinal()] = null;
        }
    }

    public BaseUser getLoggedInUser(DriveType type) {
        return this.loggedInUsers[type.ordinal()];
    }

    public List<BaseUser> getLoggedInUsers() {
        List<BaseUser> users = new LinkedList<BaseUser>();

        for (BaseUser user : this.loggedInUsers) {
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }
}
