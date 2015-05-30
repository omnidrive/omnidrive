package omnidrive.app;

import omnidrive.filesystem.FileSystem;


public class App implements Runnable {

    private final FileSystem fileSystem;

    public App(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void run() {
        this.fileSystem.startSync();
    }
}
