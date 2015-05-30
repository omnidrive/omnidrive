package omnidrive;

import omnidrive.app.App;
import omnidrive.filesystem.FileSystem;
import omnidrive.install.Install;


public class Bootstarp {

    private final FileSystem fileSystem;
    private final App app;
    private final Install install;

    public Bootstarp() {
        this.fileSystem = new FileSystem();
        this.app = new App(this.fileSystem);
        this.install = new Install(this.fileSystem);
    }

    public void start() {
        install.doFirstInstallationIfNeeded();
        app.run();
    }

    public static void main(String[] args) {
        Bootstarp boot = new Bootstarp();
        boot.start();
    }

}
