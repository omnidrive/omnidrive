package omnidrive;

import omnidrive.app.App;
import omnidrive.filesystem.FileSystem;
import omnidrive.install.Install;


public class Main {

    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        App app = new App(fileSystem);
        Install install = new Install(fileSystem);

        install.doFirstInstallationIfNeeded();

        app.run();

    }

}
