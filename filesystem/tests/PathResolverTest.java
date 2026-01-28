package filesystem.tests;

import filesystem.exceptions.NotADirectoryException;
import filesystem.exceptions.PathNotFoundException;
import filesystem.models.Directory;
import filesystem.models.File;
import filesystem.models.FileSystemNode;
import filesystem.services.PathResolver;

/**
 * Unit tests for the PathResolver service.
 */
public class PathResolverTest {
    
    private PathResolver pathResolver;
    private Directory root;
    
    public void setUp() {
        pathResolver = new PathResolver();
        root = Directory.createRoot();
        
        // Set up test structure: /home/user/docs and /home/user/file.txt
        Directory home = new Directory("home", root);
        root.addChild(home);
        
        Directory user = new Directory("user", home);
        home.addChild(user);
        
        Directory docs = new Directory("docs", user);
        user.addChild(docs);
        
        File file = new File("file.txt", user);
        user.addChild(file);
    }
    
    public void testResolveAbsolutePathToRoot() {
        setUp();
        
        FileSystemNode node = pathResolver.resolve("/", root, root);
        assert node == root : "/ should resolve to root";
        
        System.out.println("✓ testResolveAbsolutePathToRoot passed");
    }
    
    public void testResolveAbsolutePath() {
        setUp();
        
        FileSystemNode node = pathResolver.resolve("/home/user", root, root);
        assert node.getName().equals("user") : "Should resolve to user directory";
        assert node.isDirectory() : "Should be a directory";
        
        System.out.println("✓ testResolveAbsolutePath passed");
    }
    
    public void testResolveRelativePath() {
        setUp();
        
        Directory home = (Directory) root.getChild("home");
        FileSystemNode node = pathResolver.resolve("user/docs", home, root);
        
        assert node.getName().equals("docs") : "Should resolve to docs directory";
        
        System.out.println("✓ testResolveRelativePath passed");
    }
    
    public void testResolveWithParentReference() {
        setUp();
        
        Directory home = (Directory) root.getChild("home");
        Directory user = (Directory) home.getChild("user");
        Directory docs = (Directory) user.getChild("docs");
        
        FileSystemNode node = pathResolver.resolve("..", docs, root);
        assert node.getName().equals("user") : ".. should resolve to parent";
        
        System.out.println("✓ testResolveWithParentReference passed");
    }
    
    public void testResolveWithMultipleParentRefs() {
        setUp();
        
        Directory home = (Directory) root.getChild("home");
        Directory user = (Directory) home.getChild("user");
        Directory docs = (Directory) user.getChild("docs");
        
        FileSystemNode node = pathResolver.resolve("../..", docs, root);
        assert node.getName().equals("home") : "../.. should go up two levels";
        
        System.out.println("✓ testResolveWithMultipleParentRefs passed");
    }
    
    public void testResolveWithCurrentDir() {
        setUp();
        
        Directory home = (Directory) root.getChild("home");
        FileSystemNode node = pathResolver.resolve("./user", home, root);
        
        assert node.getName().equals("user") : ". should stay in current dir";
        
        System.out.println("✓ testResolveWithCurrentDir passed");
    }
    
    public void testResolveFile() {
        setUp();
        
        FileSystemNode node = pathResolver.resolve("/home/user/file.txt", root, root);
        
        assert node.getName().equals("file.txt") : "Should resolve to file";
        assert !node.isDirectory() : "Should not be a directory";
        
        System.out.println("✓ testResolveFile passed");
    }
    
    public void testResolveNonexistentPath() {
        setUp();
        
        try {
            pathResolver.resolve("/home/user/nonexistent", root, root);
            assert false : "Should throw PathNotFoundException";
        } catch (PathNotFoundException e) {
            // Expected
        }
        
        System.out.println("✓ testResolveNonexistentPath passed");
    }
    
    public void testResolveDirectoryWithFile() {
        setUp();
        
        try {
            pathResolver.resolveDirectory("/home/user/file.txt", root, root);
            assert false : "Should throw NotADirectoryException";
        } catch (NotADirectoryException e) {
            // Expected
        }
        
        System.out.println("✓ testResolveDirectoryWithFile passed");
    }
    
    public void testResolveParent() {
        setUp();
        
        PathResolver.PathResolution resolution = pathResolver.resolveParent(
            "/home/user/newfile.txt", root, root);
        
        assert resolution.getParentDirectory().getName().equals("user") : 
            "Parent should be user directory";
        assert resolution.getTargetName().equals("newfile.txt") : 
            "Target name should be newfile.txt";
        
        System.out.println("✓ testResolveParent passed");
    }
    
    public void testIsAbsolutePath() {
        setUp();
        
        assert pathResolver.isAbsolutePath("/home") : "/home should be absolute";
        assert !pathResolver.isAbsolutePath("home") : "home should be relative";
        assert !pathResolver.isAbsolutePath("./home") : "./home should be relative";
        
        System.out.println("✓ testIsAbsolutePath passed");
    }
    
    public void testParentOfRoot() {
        setUp();
        
        // Going up from root should stay at root
        FileSystemNode node = pathResolver.resolve("..", root, root);
        assert node == root : "Parent of root should be root";
        
        System.out.println("✓ testParentOfRoot passed");
    }
    
    public static void main(String[] args) {
        PathResolverTest test = new PathResolverTest();
        
        System.out.println("Running PathResolver tests...\n");
        
        test.testResolveAbsolutePathToRoot();
        test.testResolveAbsolutePath();
        test.testResolveRelativePath();
        test.testResolveWithParentReference();
        test.testResolveWithMultipleParentRefs();
        test.testResolveWithCurrentDir();
        test.testResolveFile();
        test.testResolveNonexistentPath();
        test.testResolveDirectoryWithFile();
        test.testResolveParent();
        test.testIsAbsolutePath();
        test.testParentOfRoot();
        
        System.out.println("\nAll PathResolver tests passed! ✓");
    }
}

