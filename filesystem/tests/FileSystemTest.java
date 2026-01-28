package filesystem.tests;

import filesystem.exceptions.*;
import filesystem.models.Directory;
import filesystem.models.File;
import filesystem.models.FileSystemNode;
import filesystem.services.FileSystem;

import java.util.List;

/**
 * Unit tests for the FileSystem service.
 */
public class FileSystemTest {
    
    private FileSystem fileSystem;
    
    public void setUp() {
        fileSystem = new FileSystem();
    }
    
    public void testInitialState() {
        setUp();
        
        assert fileSystem.getCurrentPath().equals("/") : "Initial path should be /";
        assert fileSystem.getRoot() != null : "Root should not be null";
        assert fileSystem.getCurrentDirectory() == fileSystem.getRoot() : "Current should be root initially";
        
        System.out.println("✓ testInitialState passed");
    }
    
    public void testCreateDirectory() {
        setUp();
        
        Directory dir = fileSystem.createDirectory("home");
        assert dir != null : "Created directory should not be null";
        assert dir.getName().equals("home") : "Directory name should be 'home'";
        assert fileSystem.exists("home") : "Directory should exist";
        
        System.out.println("✓ testCreateDirectory passed");
    }
    
    public void testCreateNestedDirectory() {
        setUp();
        
        fileSystem.createDirectory("home");
        fileSystem.createDirectory("home/user");
        fileSystem.createDirectory("home/user/documents");
        
        assert fileSystem.exists("home/user/documents") : "Nested directory should exist";
        
        System.out.println("✓ testCreateNestedDirectory passed");
    }
    
    public void testCreateFile() {
        setUp();
        
        File file = fileSystem.createFile("test.txt");
        assert file != null : "Created file should not be null";
        assert file.getName().equals("test.txt") : "File name should be 'test.txt'";
        assert fileSystem.exists("test.txt") : "File should exist";
        
        System.out.println("✓ testCreateFile passed");
    }
    
    public void testWriteAndReadFile() {
        setUp();
        
        fileSystem.writeFile("test.txt", "Hello, World!");
        String content = fileSystem.readFile("test.txt");
        
        assert content.equals("Hello, World!") : "Content should match";
        
        System.out.println("✓ testWriteAndReadFile passed");
    }
    
    public void testAppendToFile() {
        setUp();
        
        fileSystem.writeFile("test.txt", "Hello");
        fileSystem.appendToFile("test.txt", ", World!");
        String content = fileSystem.readFile("test.txt");
        
        assert content.equals("Hello, World!") : "Appended content should match";
        
        System.out.println("✓ testAppendToFile passed");
    }
    
    public void testChangeDirectory() {
        setUp();
        
        fileSystem.createDirectory("home");
        fileSystem.changeDirectory("home");
        
        assert fileSystem.getCurrentPath().equals("/home") : "Should be in /home";
        
        System.out.println("✓ testChangeDirectory passed");
    }
    
    public void testChangeDirectoryWithAbsolutePath() {
        setUp();
        
        fileSystem.createDirectory("home");
        fileSystem.createDirectory("home/user");
        fileSystem.changeDirectory("/home/user");
        
        assert fileSystem.getCurrentPath().equals("/home/user") : "Should be in /home/user";
        
        System.out.println("✓ testChangeDirectoryWithAbsolutePath passed");
    }
    
    public void testChangeDirectoryWithParentRef() {
        setUp();
        
        fileSystem.createDirectory("home");
        fileSystem.createDirectory("home/user");
        fileSystem.changeDirectory("home/user");
        fileSystem.changeDirectory("..");
        
        assert fileSystem.getCurrentPath().equals("/home") : "Should be in /home after going up";
        
        System.out.println("✓ testChangeDirectoryWithParentRef passed");
    }
    
    public void testListDirectory() {
        setUp();
        
        fileSystem.createDirectory("dir1");
        fileSystem.createDirectory("dir2");
        fileSystem.createFile("file1.txt");
        
        List<FileSystemNode> nodes = fileSystem.listDirectory(null);
        
        assert nodes.size() == 3 : "Should have 3 items";
        
        System.out.println("✓ testListDirectory passed");
    }
    
    public void testPathNotFoundThrowsException() {
        setUp();
        
        try {
            fileSystem.changeDirectory("nonexistent");
            assert false : "Should have thrown PathNotFoundException";
        } catch (PathNotFoundException e) {
            // Expected
        }
        
        System.out.println("✓ testPathNotFoundThrowsException passed");
    }
    
    public void testNotADirectoryThrowsException() {
        setUp();
        
        fileSystem.createFile("test.txt");
        
        try {
            fileSystem.changeDirectory("test.txt");
            assert false : "Should have thrown NotADirectoryException";
        } catch (NotADirectoryException e) {
            // Expected
        }
        
        System.out.println("✓ testNotADirectoryThrowsException passed");
    }
    
    public void testNotAFileThrowsException() {
        setUp();
        
        fileSystem.createDirectory("testdir");
        
        try {
            fileSystem.readFile("testdir");
            assert false : "Should have thrown NotAFileException";
        } catch (NotAFileException e) {
            // Expected
        }
        
        System.out.println("✓ testNotAFileThrowsException passed");
    }
    
    public void testPathAlreadyExistsThrowsException() {
        setUp();
        
        fileSystem.createDirectory("home");
        
        try {
            fileSystem.createDirectory("home");
            assert false : "Should have thrown PathAlreadyExistsException";
        } catch (PathAlreadyExistsException e) {
            // Expected
        }
        
        System.out.println("✓ testPathAlreadyExistsThrowsException passed");
    }
    
    public static void main(String[] args) {
        FileSystemTest test = new FileSystemTest();
        
        System.out.println("Running FileSystem tests...\n");
        
        test.testInitialState();
        test.testCreateDirectory();
        test.testCreateNestedDirectory();
        test.testCreateFile();
        test.testWriteAndReadFile();
        test.testAppendToFile();
        test.testChangeDirectory();
        test.testChangeDirectoryWithAbsolutePath();
        test.testChangeDirectoryWithParentRef();
        test.testListDirectory();
        test.testPathNotFoundThrowsException();
        test.testNotADirectoryThrowsException();
        test.testNotAFileThrowsException();
        test.testPathAlreadyExistsThrowsException();
        
        System.out.println("\nAll FileSystem tests passed! ✓");
    }
}

