package omnidrive.api.microsoft.lib.rest;

public interface RestApiErrorListener {
    void errorOccured(int code);
    boolean shouldContinueOnError(int code);
}
