package omnidrive.api.account;

public class AccountChangedEvent {
    public enum State {
        Added,
        Refreshed,
        Removed
    }

    private final Account account;
    private final State state;

    public AccountChangedEvent(Account account, State state) {
        this.account = account;
        this.state = state;
    }

    public Account getAccount() {
        return account;
    }

    public State getState() {
        return state;
    }
}
