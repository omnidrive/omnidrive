package omnidrive.api.account;

public class AccountException extends Exception {
    private int code = 0; //todo - error code?

    public AccountException(AccountType accountType, String message) {
        super(accountType.toString() + ": " + message);
    }
}
