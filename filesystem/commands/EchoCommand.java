package filesystem.commands;

import filesystem.exceptions.InvalidCommandException;
import filesystem.services.FileSystem;

/**
 * Command to write content to a file or display text.
 * Usage: echo "content" > file    - write to file (overwrite)
 *        echo "content" >> file   - append to file
 *        echo "content"           - print content
 */
public class EchoCommand implements Command {
    
    private final FileSystem fileSystem;
    
    public EchoCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            return "";
        }
        
        // Find redirection operator
        int redirectIndex = -1;
        boolean append = false;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(">")) {
                redirectIndex = i;
                append = false;
                break;
            } else if (args[i].equals(">>")) {
                redirectIndex = i;
                append = true;
                break;
            }
        }
        
        // No redirection - just echo the content
        if (redirectIndex == -1) {
            return buildContent(args, 0, args.length);
        }
        
        // Validate redirection
        if (redirectIndex == args.length - 1) {
            throw new InvalidCommandException("echo", "missing file operand after redirect");
        }
        
        String content = buildContent(args, 0, redirectIndex);
        String filePath = args[redirectIndex + 1];
        
        if (append) {
            fileSystem.appendToFile(filePath, content);
        } else {
            fileSystem.writeFile(filePath, content);
        }
        
        return "";
    }
    
    private String buildContent(String[] args, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (i > start) {
                sb.append(" ");
            }
            String arg = args[i];
            // Remove surrounding quotes if present
            if ((arg.startsWith("\"") && arg.endsWith("\"")) || 
                (arg.startsWith("'") && arg.endsWith("'"))) {
                arg = arg.substring(1, arg.length() - 1);
            }
            sb.append(arg);
        }
        return sb.toString();
    }
    
    @Override
    public String getName() {
        return "echo";
    }
    
    @Override
    public String getUsage() {
        return "echo <text> [> file | >> file] - print text or write to file";
    }
}

