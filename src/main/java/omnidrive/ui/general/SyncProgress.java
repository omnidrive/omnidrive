package omnidrive.ui.general;

public enum SyncProgress {
    Ready,
    Syncing,
    Error,
    Stopped;

    @Override
    public String toString() {
        switch (this) {
            case Ready:
                return "Ready.";
            case Syncing:
                return "Syncing...";
            case Error:
                return "Some error occured.";
            case Stopped:
                return "Stopped.";
        }

        return "";
    }
}