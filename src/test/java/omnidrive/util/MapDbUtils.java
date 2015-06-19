package omnidrive.util;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;

public class MapDbUtils {

    public static DB createMemoryDb() {
        return DBMaker.newMemoryDB().make();
    }

    public static DB createFileDb(File file) {
        return DBMaker.newFileDB(file)
                .closeOnJvmShutdown()
                .make();
    }

}
