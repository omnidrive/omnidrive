package omnidrive.api.box;

import omnidrive.api.base.BaseException;

public class BoxException extends BaseException {
    public BoxException(String message) {
        super("Box: " + message);
    }

    public BoxException(int responseCode) {
        super("File already exist.");
    }
}
