package omnidrive.exceptions;

import java.io.File;

public class UnableToDeleteFileException extends Exception {

    public UnableToDeleteFileException(File file) {
        super("Unable to delete" + file);
    }

}
