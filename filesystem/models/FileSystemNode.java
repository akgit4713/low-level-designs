package filesystem.models;

import filesystem.enums.NodeType;

/**
 * Abstract base class for file system nodes (Composite Pattern).
 * Represents both files and directories in the hierarchical structure.
 */
public abstract class FileSystemNode {
    
    protected final String name;
    protected Directory parent;
    protected final NodeMetadata metadata;
    
    protected FileSystemNode(String name, Directory parent) {
        validateName(name);
        this.name = name;
        this.parent = parent;
        this.metadata = new NodeMetadata();
    }
    
    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Node name cannot be null or empty");
        }
        if (name.contains("/")) {
            throw new IllegalArgumentException("Node name cannot contain '/'");
        }
    }
    
    public String getName() {
        return name;
    }
    
    public Directory getParent() {
        return parent;
    }
    
    public void setParent(Directory parent) {
        this.parent = parent;
    }
    
    public NodeMetadata getMetadata() {
        return metadata;
    }
    
    /**
     * Returns the absolute path of this node.
     */
    public String getPath() {
        if (parent == null) {
            return "/";
        }
        String parentPath = parent.getPath();
        if (parentPath.equals("/")) {
            return "/" + name;
        }
        return parentPath + "/" + name;
    }
    
    /**
     * Returns true if this node is a directory.
     */
    public abstract boolean isDirectory();
    
    /**
     * Returns the type of this node.
     */
    public abstract NodeType getType();
    
    /**
     * Returns the size of this node in bytes.
     * For files, this is the content length.
     * For directories, this is the number of children.
     */
    public abstract long getSize();
    
    @Override
    public String toString() {
        return name;
    }
}

