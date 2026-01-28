package filesystem.services;

import filesystem.exceptions.*;
import filesystem.models.Directory;
import filesystem.models.File;
import filesystem.models.FileSystemNode;
import filesystem.services.PathResolver.PathResolution;

import java.util.List;

/**
 * Core file system service managing the root and current directory.
 * Provides high-level operations for file system manipulation.
 */
public class FileSystem {
    
    private final Directory root;
    private Directory currentDirectory;
    private final PathResolver pathResolver;
    
    public FileSystem() {
        this.root = Directory.createRoot();
        this.currentDirectory = root;
        this.pathResolver = new PathResolver();
    }
    
    public FileSystem(PathResolver pathResolver) {
        this.root = Directory.createRoot();
        this.currentDirectory = root;
        this.pathResolver = pathResolver;
    }
    
    public Directory getRoot() {
        return root;
    }
    
    public Directory getCurrentDirectory() {
        return currentDirectory;
    }
    
    public String getCurrentPath() {
        return currentDirectory.getPath();
    }
    
    /**
     * Changes the current working directory.
     *
     * @param path The path to change to (absolute or relative)
     * @throws PathNotFoundException if the path doesn't exist
     * @throws NotADirectoryException if the path is not a directory
     */
    public void changeDirectory(String path) {
        Directory newDir = pathResolver.resolveDirectory(path, currentDirectory, root);
        this.currentDirectory = newDir;
    }
    
    /**
     * Creates a new directory at the specified path.
     *
     * @param path The path for the new directory
     * @return The created directory
     * @throws PathNotFoundException if the parent path doesn't exist
     * @throws PathAlreadyExistsException if a node already exists at that path
     */
    public Directory createDirectory(String path) {
        PathResolution resolution = pathResolver.resolveParent(path, currentDirectory, root);
        
        if (resolution.getParentDirectory().hasChild(resolution.getTargetName())) {
            throw new PathAlreadyExistsException(path);
        }
        
        Directory newDir = new Directory(resolution.getTargetName(), resolution.getParentDirectory());
        resolution.getParentDirectory().addChild(newDir);
        return newDir;
    }
    
    /**
     * Creates a new file at the specified path.
     *
     * @param path The path for the new file
     * @return The created file
     * @throws PathNotFoundException if the parent path doesn't exist
     * @throws PathAlreadyExistsException if a node already exists at that path
     */
    public File createFile(String path) {
        PathResolution resolution = pathResolver.resolveParent(path, currentDirectory, root);
        
        if (resolution.getParentDirectory().hasChild(resolution.getTargetName())) {
            throw new PathAlreadyExistsException(path);
        }
        
        File newFile = new File(resolution.getTargetName(), resolution.getParentDirectory());
        resolution.getParentDirectory().addChild(newFile);
        return newFile;
    }
    
    /**
     * Gets a file at the specified path, creating it if it doesn't exist.
     *
     * @param path The path to the file
     * @return The file at the specified path
     * @throws NotAFileException if the path exists but is a directory
     */
    public File getOrCreateFile(String path) {
        try {
            FileSystemNode node = pathResolver.resolve(path, currentDirectory, root);
            if (node.isDirectory()) {
                throw new NotAFileException(path);
            }
            return (File) node;
        } catch (PathNotFoundException e) {
            return createFile(path);
        }
    }
    
    /**
     * Reads the content of a file.
     *
     * @param path The path to the file
     * @return The file content
     * @throws PathNotFoundException if the file doesn't exist
     * @throws NotAFileException if the path is a directory
     */
    public String readFile(String path) {
        FileSystemNode node = pathResolver.resolve(path, currentDirectory, root);
        if (node.isDirectory()) {
            throw new NotAFileException(path);
        }
        return ((File) node).getContent();
    }
    
    /**
     * Writes content to a file, replacing existing content.
     *
     * @param path The path to the file
     * @param content The content to write
     * @throws PathNotFoundException if the parent directory doesn't exist
     * @throws NotAFileException if the path is a directory
     */
    public void writeFile(String path, String content) {
        File file = getOrCreateFile(path);
        file.setContent(content);
    }
    
    /**
     * Appends content to a file.
     *
     * @param path The path to the file
     * @param content The content to append
     */
    public void appendToFile(String path, String content) {
        File file = getOrCreateFile(path);
        file.appendContent(content);
    }
    
    /**
     * Lists the contents of a directory.
     *
     * @param path The path to list (null or empty for current directory)
     * @return List of nodes in the directory
     * @throws NotADirectoryException if the path is not a directory
     */
    public List<FileSystemNode> listDirectory(String path) {
        Directory dir;
        if (path == null || path.isEmpty()) {
            dir = currentDirectory;
        } else {
            dir = pathResolver.resolveDirectory(path, currentDirectory, root);
        }
        return dir.getChildrenList();
    }
    
    /**
     * Resolves a path to a FileSystemNode.
     */
    public FileSystemNode resolvePath(String path) {
        return pathResolver.resolve(path, currentDirectory, root);
    }
    
    /**
     * Checks if a path exists.
     */
    public boolean exists(String path) {
        try {
            pathResolver.resolve(path, currentDirectory, root);
            return true;
        } catch (PathNotFoundException e) {
            return false;
        }
    }
}

