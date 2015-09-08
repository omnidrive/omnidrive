package omnidrive.api.account;

public class AccountException extends Exception {
    private int code = 0; //todo - error code?

    public AccountException(AccountType accountType, String message, Exception original) {
        super(accountType.toString() + ": " + message);
        if (original != null) {
            System.out.println("Original Exception (" + accountType.toString() + "): " + original.getMessage());
            original.printStackTrace();
        }
    }
}
