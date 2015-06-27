package omnidrive.filesystem.sync;

import omnidrive.api.managers.AccountsManager;
import omnidrive.filesystem.manifest.Manifest;

import java.nio.file.Path;

public class DownstreamSync {

    final private Path root;

    final private AccountsManager accountsManager;

    public DownstreamSync(Path root, AccountsManager accountsManager) {
        this.root = root;
        this.accountsManager = accountsManager;
    }

    public void download(Manifest manifest) {

    }

}
