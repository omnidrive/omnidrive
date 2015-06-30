package omnidrive.filesystem.manifest;

import omnidrive.api.auth.AuthToken;
import omnidrive.api.base.DriveType;
import omnidrive.filesystem.manifest.entry.Entry;
import omnidrive.filesystem.manifest.entry.Tree;

import java.util.Map;

public interface Manifest {

    Map<DriveType, AuthToken> getAuthTokens();

    void put(DriveType driveType, AuthToken authToken);

    void put(Entry entry);

    void remove(Entry entry);

    <T extends Entry> T get(String id, Class<T> clazz);

    Tree getRoot();

    long getUpdatedTime();

}
