package omnidrive.filesystem;

import com.google.inject.AbstractModule;
import omnidrive.filesystem.manifest.MapDbManifest;
import omnidrive.filesystem.manifest.Manifest;
import omnidrive.filesystem.sync.SimpleUploadStrategy;
import omnidrive.filesystem.sync.SyncHandler;
import omnidrive.filesystem.sync.UploadStrategy;
import omnidrive.filesystem.watcher.Handler;
import omnidrive.filesystem.watcher.Watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class FileSystemModule extends AbstractModule {

    protected void configure() {
        try {
            bind(UploadStrategy.class).to(SimpleUploadStrategy.class);
            bind(Manifest.class).to(MapDbManifest.class);

            bind(WatchService.class).toInstance(FileSystems.getDefault().newWatchService());
            bind(Handler.class).to(SyncHandler.class);
            bind(Watcher.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
