package omnidrive.filesystem.sync;

import omnidrive.algo.TreeNode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileNode implements TreeNode<FileNode> {

    final private File file;

    public FileNode(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public Map<String, FileNode> getChildren() {
        Map<String, FileNode> children = new HashMap<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File child : files) {
                children.put(child.getName(), new FileNode(child));
            }
        }
        return children;
    }

}
