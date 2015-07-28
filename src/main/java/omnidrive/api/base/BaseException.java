package omnidrive.api.base;

public class BaseException extends Exception {
    private int code = 0; //todo - error code?

    public BaseException(AccountType accountType, String message) {
        super(accountType.toString() + ": " + message);
    }
}
