package omnidrive.filesystem.sync;

import omnidrive.algo.TreeNode;
import omnidrive.filesystem.watcher.Filter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileNode implements TreeNode<FileNode> {

    final private File file;

    final private Filter filter;

    public FileNode(File file) {
        this(file, null);
    }

    public FileNode(File file, Filter filter) {
        this.file = file;
        this.filter = filter;
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
                if (filter != null && filter.shouldIgnore(child)) {
                    continue;
                }
                children.put(child.getName(), new FileNode(child));
            }
        }
        return children;
    }

}
