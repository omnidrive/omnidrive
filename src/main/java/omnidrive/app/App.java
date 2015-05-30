package omnidrive.app;

/**
 * Created by assafey on 5/30/15.
 */
public class App implements Runnable {

    private final Filesystem filesystem;

    public App(Filesystem filesystem) {
        this.filesystem = filesystem;
    }

    public void run() {
        this.filesystem.startSync();
    }
}
