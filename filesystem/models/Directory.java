package filesystem.models;

import filesystem.enums.NodeType;
import filesystem.exceptions.PathAlreadyExistsException;
import filesystem.exceptions.PathNotFoundException;

import java.util.*;

/**
 * Represents a directory in the file system (Composite in Composite Pattern).
 * Directories can contain files and other directories.
 */
public class Directory extends FileSystemNode {
    
    private final Map<String, FileSystemNode> children;
    
    public Directory(String name, Directory parent) {
        super(name, parent);
        this.children = new LinkedHashMap<>(); // Maintains insertion order
    }
    
    /**
     * Creates the root directory.
     */
    public static Directory createRoot() {
        return new Directory("/", null) {
            @Override
            public String getPath() {
                return "/";
            }
            
            @Override
            public String getName() {
                return "/";
            }
        };
    }
    
    public Map<String, FileSystemNode> getChildren() {
        return Collections.unmodifiableMap(children);
    }
    
    public List<FileSystemNode> getChildrenList() {
        return new ArrayList<>(children.values());
    }
    
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }
    
    public FileSystemNode getChild(String name) {
        FileSystemNode child = children.get(name);
        if (child == null) {
            throw new PathNotFoundException(name);
        }
        return child;
    }
    
    public Optional<FileSystemNode> findChild(String name) {
        return Optional.ofNullable(children.get(name));
    }
    
    public void addChild(FileSystemNode node) {
        if (children.containsKey(node.getName())) {
            throw new PathAlreadyExistsException(node.getName());
        }
        children.put(node.getName(), node);
        node.setParent(this);
        this.metadata.updateModifiedTime();
    }
    
    public FileSystemNode removeChild(String name) {
        FileSystemNode removed = children.remove(name);
        if (removed == null) {
            throw new PathNotFoundException(name);
        }
        this.metadata.updateModifiedTime();
        return removed;
    }
    
    @Override
    public boolean isDirectory() {
        return true;
    }
    
    @Override
    public NodeType getType() {
        return NodeType.DIRECTORY;
    }
    
    @Override
    public long getSize() {
        return children.size();
    }
    
    public boolean isEmpty() {
        return children.isEmpty();
    }
}

