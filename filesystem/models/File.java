package filesystem.models;

import filesystem.enums.NodeType;

/**
 * Represents a file in the file system (Leaf in Composite Pattern).
 * Files store string content and cannot have children.
 */
public class File extends FileSystemNode {
    
    private String content;
    
    public File(String name, Directory parent) {
        super(name, parent);
        this.content = "";
    }
    
    public File(String name, Directory parent, String content) {
        super(name, parent);
        this.content = content != null ? content : "";
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content != null ? content : "";
        this.metadata.updateModifiedTime();
    }
    
    public void appendContent(String content) {
        if (content != null) {
            this.content += content;
            this.metadata.updateModifiedTime();
        }
    }
    
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    @Override
    public NodeType getType() {
        return NodeType.FILE;
    }
    
    @Override
    public long getSize() {
        return content.length();
    }
}

