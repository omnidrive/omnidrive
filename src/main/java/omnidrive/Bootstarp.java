package omnidrive;

import com.google.inject.Guice;
import com.google.inject.Injector;
import omnidrive.app.App;
import omnidrive.filesystem.FileSystem;
import omnidrive.filesystem.FileSystemModule;
import omnidrive.install.Install;


public class Bootstarp {

    private final FileSystem fileSystem;
    private final App app;
    private final Install install;


    public Bootstarp() {

        Injector injector = Guice.createInjector(new FileSystemModule());
        FileSystem fs = injector.getInstance(FileSystem.class);

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
