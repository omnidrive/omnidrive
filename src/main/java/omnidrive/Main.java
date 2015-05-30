package omnidrive;

import omnidrive.app.App;
import omnidrive.install.Install;


public class Main {

    public static void main(String[] args) {
        Filesystem filesystem = new FileSystem();
        App app = new App(filesystem);
        Install install = new Install(filesystem);

        install.doFirstInstallationIfNeeded();

        app.run();

    }

}
