package filesystem.tests;

import filesystem.commands.CommandFactory;
import filesystem.services.FileSystem;
import filesystem.services.Shell;
import filesystem.strategies.ListingStrategyFactory;

/**
 * Unit tests for the Shell and Command classes.
 */
public class ShellTest {
    
    private FileSystem fileSystem;
    private Shell shell;
    
    public void setUp() {
        fileSystem = new FileSystem();
        ListingStrategyFactory listingStrategyFactory = new ListingStrategyFactory();
        CommandFactory commandFactory = new CommandFactory(fileSystem, listingStrategyFactory);
        shell = new Shell(fileSystem, commandFactory);
    }
    
    public void testPwdCommand() {
        setUp();
        
        String result = shell.execute("pwd");
        assert result.equals("/") : "pwd should return /";
        
        System.out.println("✓ testPwdCommand passed");
    }
    
    public void testMkdirCommand() {
        setUp();
        
        shell.execute("mkdir testdir");
        assert fileSystem.exists("testdir") : "Directory should exist";
        
        System.out.println("✓ testMkdirCommand passed");
    }
    
    public void testCdCommand() {
        setUp();
        
        shell.execute("mkdir home");
        shell.execute("cd home");
        String result = shell.execute("pwd");
        
        assert result.equals("/home") : "Should be in /home";
        
        System.out.println("✓ testCdCommand passed");
    }
    
    public void testTouchCommand() {
        setUp();
        
        shell.execute("touch test.txt");
        assert fileSystem.exists("test.txt") : "File should exist";
        
        System.out.println("✓ testTouchCommand passed");
    }
    
    public void testEchoToFile() {
        setUp();
        
        shell.execute("echo \"Hello, World!\" > test.txt");
        String content = fileSystem.readFile("test.txt");
        
        assert content.equals("Hello, World!") : "Content should match";
        
        System.out.println("✓ testEchoToFile passed");
    }
    
    public void testEchoAppendToFile() {
        setUp();
        
        shell.execute("echo \"Hello\" > test.txt");
        shell.execute("echo \", World!\" >> test.txt");
        String content = fileSystem.readFile("test.txt");
        
        assert content.equals("Hello, World!") : "Appended content should match";
        
        System.out.println("✓ testEchoAppendToFile passed");
    }
    
    public void testCatCommand() {
        setUp();
        
        shell.execute("echo \"Test content\" > test.txt");
        String result = shell.execute("cat test.txt");
        
        assert result.equals("Test content") : "cat should return file content";
        
        System.out.println("✓ testCatCommand passed");
    }
    
    public void testLsCommand() {
        setUp();
        
        shell.execute("mkdir dir1");
        shell.execute("touch file1.txt");
        String result = shell.execute("ls");
        
        assert result.contains("dir1") : "ls should list dir1";
        assert result.contains("file1.txt") : "ls should list file1.txt";
        
        System.out.println("✓ testLsCommand passed");
    }
    
    public void testLsDetailedCommand() {
        setUp();
        
        shell.execute("mkdir testdir");
        shell.execute("touch testfile.txt");
        String result = shell.execute("ls -l");
        
        assert result.contains("drwx") : "ls -l should show directory permissions";
        assert result.contains("-rw-") : "ls -l should show file permissions";
        assert result.contains("total") : "ls -l should show total";
        
        System.out.println("✓ testLsDetailedCommand passed");
    }
    
    public void testAbsolutePathNavigation() {
        setUp();
        
        shell.execute("mkdir home");
        shell.execute("mkdir home/user");
        shell.execute("mkdir home/user/docs");
        shell.execute("cd /home/user/docs");
        
        String result = shell.execute("pwd");
        assert result.equals("/home/user/docs") : "Should navigate with absolute path";
        
        System.out.println("✓ testAbsolutePathNavigation passed");
    }
    
    public void testRelativePathNavigation() {
        setUp();
        
        shell.execute("mkdir home");
        shell.execute("mkdir home/user");
        shell.execute("cd home/user");
        shell.execute("cd ..");
        
        String result = shell.execute("pwd");
        assert result.equals("/home") : "Should navigate up with ..";
        
        System.out.println("✓ testRelativePathNavigation passed");
    }
    
    public void testHelpCommand() {
        setUp();
        
        String result = shell.execute("help");
        
        assert result.contains("mkdir") : "Help should mention mkdir";
        assert result.contains("cd") : "Help should mention cd";
        assert result.contains("ls") : "Help should mention ls";
        
        System.out.println("✓ testHelpCommand passed");
    }
    
    public void testEmptyInput() {
        setUp();
        
        String result = shell.execute("");
        assert result.isEmpty() : "Empty input should return empty string";
        
        result = shell.execute("   ");
        assert result.isEmpty() : "Whitespace input should return empty string";
        
        System.out.println("✓ testEmptyInput passed");
    }
    
    public void testInvalidCommand() {
        setUp();
        
        try {
            shell.execute("invalidcmd");
            assert false : "Should throw exception for invalid command";
        } catch (Exception e) {
            assert e.getMessage().contains("not found") : "Should indicate command not found";
        }
        
        System.out.println("✓ testInvalidCommand passed");
    }
    
    public void testMultipleFiles() {
        setUp();
        
        shell.execute("touch file1.txt file2.txt file3.txt");
        
        assert fileSystem.exists("file1.txt") : "file1 should exist";
        assert fileSystem.exists("file2.txt") : "file2 should exist";
        assert fileSystem.exists("file3.txt") : "file3 should exist";
        
        System.out.println("✓ testMultipleFiles passed");
    }
    
    public static void main(String[] args) {
        ShellTest test = new ShellTest();
        
        System.out.println("Running Shell tests...\n");
        
        test.testPwdCommand();
        test.testMkdirCommand();
        test.testCdCommand();
        test.testTouchCommand();
        test.testEchoToFile();
        test.testEchoAppendToFile();
        test.testCatCommand();
        test.testLsCommand();
        test.testLsDetailedCommand();
        test.testAbsolutePathNavigation();
        test.testRelativePathNavigation();
        test.testHelpCommand();
        test.testEmptyInput();
        test.testInvalidCommand();
        test.testMultipleFiles();
        
        System.out.println("\nAll Shell tests passed! ✓");
    }
}

