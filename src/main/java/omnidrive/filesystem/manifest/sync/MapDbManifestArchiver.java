package omnidrive.filesystem.manifest.sync;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;
import org.kamranzafar.jtar.TarOutputStream;

import java.io.*;
import java.nio.file.Path;

public class MapDbManifestArchiver {

    public static final int BUFFER_SIZE = 2048;

    final private File file;

    public MapDbManifestArchiver(File file) {
        this.file = file;
    }

    public File archive() throws IOException {
        File archive = File.createTempFile("manifest", "tar");

        FileOutputStream dest = new FileOutputStream(archive);
        TarOutputStream out = new TarOutputStream(new BufferedOutputStream(dest));
        for (File file : getFilesToTar()) {
            out.putNextEntry(new TarEntry(file, file.getName()));
            BufferedInputStream origin = new BufferedInputStream(new FileInputStream(file));
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            while ((count = origin.read(data)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
            origin.close();
        }
        out.close();

        return archive;
    }

    public void extract(File tar, Path destDir) throws IOException {
        FileInputStream source = new FileInputStream(tar);
        TarInputStream in = new TarInputStream(new BufferedInputStream(source));
        TarEntry entry;
        while((entry = in.getNextEntry()) != null) {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            Path destPath = destDir.resolve(entry.getName());
            FileOutputStream fos = new FileOutputStream(destPath.toFile());
            BufferedOutputStream dest = new BufferedOutputStream(fos);
            while((count = in.read(data)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
        }
        in.close();
    }

    private File[] getFilesToTar() {
        Path parent = file.toPath().getParent();
        String name = file.getName();

        File[] files = new File[3];
        files[0] = file;
        files[1] = parent.resolve(name + ".p").toFile();
        files[2] = parent.resolve(name + ".t").toFile();

        return files;
    }

}
