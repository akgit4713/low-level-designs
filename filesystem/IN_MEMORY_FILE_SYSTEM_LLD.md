# In-Memory File System - Low-Level Design

## Table of Contents
1. [Requirements Overview](#requirements-overview)
2. [Architecture & Design](#architecture--design)
3. [Class Diagram](#class-diagram)
4. [Component Details](#component-details)
5. [Design Patterns](#design-patterns)
6. [SOLID Principles Applied](#solid-principles-applied)
7. [Extension Points](#extension-points)
8. [Usage Examples](#usage-examples)

---

## Requirements Overview

### Functional Requirements
- Support creation of files and directories organized in a hierarchical structure
- The entire file system state must be stored in memory
- Interaction through a shell that parses and executes string-based commands
- Commands supported: `mkdir`, `cd`, `touch`, `ls`, `pwd`, `cat`, `echo`
- `ls` command supports simple format and detailed (`-l`) format
- Handle both absolute (`/home/user`) and relative (`documents`, `../`) paths
- Files store simple string-based content

### Non-Functional Requirements
- Modularity with clear separation of concerns
- Maintainability with clean, testable code
- Extensibility for new commands and listing strategies
- Clear error handling with helpful messages
- Intuitive API for file system operations

---

## Architecture & Design

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                              Shell                                   │
│                    (Command parsing & execution)                     │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    ▼              ▼              ▼
              ┌──────────┐  ┌──────────┐  ┌──────────────────┐
              │ Command  │  │ Command  │  │    Command       │
              │ Factory  │  │   (*)    │  │ (mkdir,cd,ls...) │
              └──────────┘  └──────────┘  └──────────────────┘
                    │                              │
                    ▼                              ▼
              ┌───────────────────────────────────────────────┐
              │                  FileSystem                    │
              │         (Core file system operations)          │
              └───────────────────────────────────────────────┘
                    │                              │
          ┌─────────┴─────────┐          ┌────────┴────────┐
          ▼                   ▼          ▼                 ▼
    ┌────────────┐     ┌────────────┐  ┌─────────────┐ ┌──────────┐
    │ PathResolver│    │  Directory  │  │    File     │ │ Listing  │
    │             │    │   (Root)    │  │             │ │ Strategy │
    └────────────┘     └────────────┘  └─────────────┘ └──────────┘
```

### Package Structure

```
filesystem/
├── enums/
│   └── NodeType.java
├── exceptions/
│   ├── FileSystemException.java
│   ├── PathNotFoundException.java
│   ├── NotADirectoryException.java
│   ├── NotAFileException.java
│   ├── PathAlreadyExistsException.java
│   └── InvalidCommandException.java
├── models/
│   ├── FileSystemNode.java (Abstract)
│   ├── File.java
│   ├── Directory.java
│   └── NodeMetadata.java
├── services/
│   ├── FileSystem.java
│   ├── PathResolver.java
│   └── Shell.java
├── commands/
│   ├── Command.java (Interface)
│   ├── CommandFactory.java
│   ├── MkdirCommand.java
│   ├── CdCommand.java
│   ├── TouchCommand.java
│   ├── LsCommand.java
│   ├── PwdCommand.java
│   ├── CatCommand.java
│   └── EchoCommand.java
├── strategies/
│   ├── ListingStrategy.java (Interface)
│   ├── SimpleListingStrategy.java
│   ├── DetailedListingStrategy.java
│   └── ListingStrategyFactory.java
├── tests/
│   ├── FileSystemTest.java
│   ├── ShellTest.java
│   ├── PathResolverTest.java
│   └── AllTests.java
└── Main.java
```

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            <<abstract>>                                      │
│                          FileSystemNode                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│ - name: String                                                              │
│ - parent: Directory                                                          │
│ - metadata: NodeMetadata                                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ + getName(): String                                                          │
│ + getParent(): Directory                                                     │
│ + getPath(): String                                                          │
│ + getMetadata(): NodeMetadata                                                │
│ + isDirectory(): boolean {abstract}                                          │
│ + getType(): NodeType {abstract}                                             │
│ + getSize(): long {abstract}                                                 │
└─────────────────────────────────────────────────────────────────────────────┘
                    ▲                                    ▲
                    │                                    │
        ┌───────────┴───────────┐          ┌────────────┴────────────┐
        │         File          │          │       Directory          │
        ├───────────────────────┤          ├─────────────────────────┤
        │ - content: String     │          │ - children: Map<String, │
        ├───────────────────────┤          │     FileSystemNode>     │
        │ + getContent(): String│          ├─────────────────────────┤
        │ + setContent(String)  │          │ + getChildren(): Map    │
        │ + appendContent(String│          │ + addChild(Node)        │
        │ + isDirectory(): false│          │ + removeChild(String)   │
        │ + getSize(): long     │          │ + hasChild(String)      │
        └───────────────────────┘          │ + isDirectory(): true   │
                                           └─────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│                            <<interface>>                                     │
│                              Command                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│ + execute(args: String[]): String                                            │
│ + getName(): String                                                          │
│ + getUsage(): String                                                         │
└─────────────────────────────────────────────────────────────────────────────┘
        ▲           ▲           ▲           ▲           ▲           ▲
        │           │           │           │           │           │
    ┌───┴───┐   ┌───┴───┐   ┌───┴───┐   ┌───┴───┐   ┌───┴───┐   ┌───┴───┐
    │ Mkdir │   │  Cd   │   │ Touch │   │  Ls   │   │  Pwd  │   │ Cat/  │
    │Command│   │Command│   │Command│   │Command│   │Command│   │ Echo  │
    └───────┘   └───────┘   └───────┘   └───────┘   └───────┘   └───────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│                            <<interface>>                                     │
│                          ListingStrategy                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ + format(nodes: List<FileSystemNode>): String                                │
│ + getName(): String                                                          │
└─────────────────────────────────────────────────────────────────────────────┘
                    ▲                                    ▲
                    │                                    │
        ┌───────────┴───────────┐          ┌────────────┴────────────┐
        │ SimpleListingStrategy │          │ DetailedListingStrategy │
        │                       │          │        (-l flag)        │
        └───────────────────────┘          └─────────────────────────┘
```

---

## Component Details

### 1. Models

| Class | Purpose |
|-------|---------|
| `FileSystemNode` | Abstract base class for all file system entities. Provides common properties (name, parent, metadata) and abstract methods for polymorphism |
| `File` | Leaf node representing a file. Stores string content |
| `Directory` | Composite node representing a directory. Contains children (files and subdirectories) |
| `NodeMetadata` | Value object storing timestamps (created, modified) for nodes |

### 2. Services

| Class | Purpose |
|-------|---------|
| `FileSystem` | Core service managing root directory, current directory, and high-level operations |
| `PathResolver` | Resolves absolute and relative paths to FileSystemNode instances |
| `Shell` | Parses user input, tokenizes commands, and dispatches to appropriate Command instances |

### 3. Commands

| Command | Description |
|---------|-------------|
| `mkdir` | Creates directories (supports multiple paths) |
| `cd` | Changes current working directory |
| `touch` | Creates empty files (supports multiple files) |
| `ls` | Lists directory contents (supports `-l` for detailed view) |
| `pwd` | Prints current working directory |
| `cat` | Displays file contents |
| `echo` | Prints text or writes to files (`>` overwrite, `>>` append) |

### 4. Strategies

| Strategy | Description |
|----------|-------------|
| `SimpleListingStrategy` | Names only, space-separated (default `ls`) |
| `DetailedListingStrategy` | Full details with permissions, size, timestamp (`ls -l`) |

---

## Design Patterns

### 1. Composite Pattern
**Where:** `FileSystemNode` → `File` / `Directory`

**Why:** Models the hierarchical tree structure of a file system where directories contain files and other directories. Both are treated uniformly through the base abstraction.

```
FileSystemNode (Component)
    ├── File (Leaf)
    └── Directory (Composite) ──contains──> FileSystemNode*
```

### 2. Command Pattern
**Where:** `Command` interface with `MkdirCommand`, `CdCommand`, etc.

**Why:** 
- Decouples the invoker (Shell) from the receiver (FileSystem)
- Each command encapsulates its execution logic
- Easy to add new commands without modifying Shell
- Supports undo/redo (future extension)

```
Shell (Invoker) ──uses──> Command (Interface)
                              │
                     ┌────────┼────────┐
                     ▼        ▼        ▼
                  Mkdir     Cd       Ls...
```

### 3. Strategy Pattern
**Where:** `ListingStrategy` interface with `Simple` and `Detailed` implementations

**Why:**
- Different algorithms for the same operation (formatting ls output)
- Switch strategies at runtime based on flags
- Easy to add new listing formats (e.g., JSON, tree view)

```
LsCommand ──uses──> ListingStrategy
                        │
               ┌────────┴────────┐
               ▼                 ▼
          SimpleStrategy    DetailedStrategy
```

### 4. Factory Pattern
**Where:** `CommandFactory`, `ListingStrategyFactory`

**Why:**
- Centralizes object creation
- Encapsulates registration of commands/strategies
- Supports dependency injection
- Makes system extensible without modifying client code

---

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
Each class has one reason to change:
- `PathResolver` - Only handles path resolution logic
- `FileSystem` - Only manages file system state and operations
- `Shell` - Only parses input and dispatches commands
- Each `Command` - Only handles its specific operation

### Open/Closed Principle (OCP)
System is open for extension, closed for modification:
- Add new commands by implementing `Command` interface
- Add new listing formats by implementing `ListingStrategy`
- Register with factories - no core code changes needed

### Liskov Substitution Principle (LSP)
Subtypes are substitutable for base types:
- `File` and `Directory` are substitutable for `FileSystemNode`
- All `Command` implementations are substitutable for `Command` interface
- All `ListingStrategy` implementations work wherever interface is expected

### Interface Segregation Principle (ISP)
Interfaces are focused and minimal:
- `Command` interface has only 3 methods
- `ListingStrategy` has only 2 methods
- No forced implementation of unused methods

### Dependency Inversion Principle (DIP)
High-level modules depend on abstractions:
- `Shell` depends on `Command` interface, not concrete commands
- `LsCommand` depends on `ListingStrategy` interface
- Dependencies injected via constructors

---

## Extension Points

### 1. Adding New Commands
```java
public class RmCommand implements Command {
    private final FileSystem fileSystem;
    
    public RmCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    @Override
    public String execute(String[] args) {
        // Implementation
    }
    
    @Override
    public String getName() { return "rm"; }
    
    @Override
    public String getUsage() { return "rm <path> - remove file or directory"; }
}

// Register it
commandFactory.register(new RmCommand(fileSystem));
```

### 2. Adding New Listing Strategies
```java
public class JsonListingStrategy implements ListingStrategy {
    @Override
    public String format(List<FileSystemNode> nodes) {
        // Return JSON formatted string
    }
    
    @Override
    public String getName() { return "json"; }
}

// Register it
listingStrategyFactory.register(new JsonListingStrategy());
```

### 3. Adding File Permissions
```java
public class Permission {
    private boolean read;
    private boolean write;
    private boolean execute;
    // ...
}

// Add to NodeMetadata
public class NodeMetadata {
    private Permission ownerPermission;
    private Permission groupPermission;
    private Permission otherPermission;
    // ...
}
```

### 4. Adding Symbolic Links
```java
public class SymbolicLink extends FileSystemNode {
    private FileSystemNode target;
    
    @Override
    public boolean isDirectory() {
        return target.isDirectory();
    }
}
```

---

## Usage Examples

### Programmatic API Usage
```java
// Initialize
FileSystem fileSystem = new FileSystem();
ListingStrategyFactory listingFactory = new ListingStrategyFactory();
CommandFactory commandFactory = new CommandFactory(fileSystem, listingFactory);
Shell shell = new Shell(fileSystem, commandFactory);

// Execute commands
shell.execute("mkdir home");
shell.execute("mkdir home/user");
shell.execute("cd home/user");
shell.execute("touch notes.txt");
shell.execute("echo \"Hello, World!\" > notes.txt");

String content = shell.execute("cat notes.txt");  // "Hello, World!"
String listing = shell.execute("ls -l");

// Direct API usage
fileSystem.createDirectory("projects");
fileSystem.writeFile("projects/readme.txt", "Project documentation");
String text = fileSystem.readFile("projects/readme.txt");
```

### Interactive Shell Session
```
/ $ mkdir home
/ $ mkdir home/user
/ $ cd home/user
/home/user $ touch notes.txt
/home/user $ echo "Hello, World!" > notes.txt
/home/user $ cat notes.txt
Hello, World!
/home/user $ ls
notes.txt
/home/user $ ls -l
total 1
-rw-r--r--  1 user  user        13 Dec 30 10:30 notes.txt
/home/user $ pwd
/home/user
/home/user $ cd ..
/home $ cd /
/ $ ls
home/
```

---

## Error Handling

| Exception | When Thrown |
|-----------|-------------|
| `PathNotFoundException` | Accessing non-existent path |
| `NotADirectoryException` | `cd` into a file, or traversing through a file |
| `NotAFileException` | `cat` on a directory |
| `PathAlreadyExistsException` | Creating file/directory that already exists |
| `InvalidCommandException` | Unknown command or missing arguments |

Example:
```
/ $ cd nonexistent
Error: Path not found: nonexistent

/ $ touch file.txt
/ $ cd file.txt
Error: Not a directory: file.txt
```

---

## Running the Application

### Compile
```bash
cd /path/to/low-level-designs
javac filesystem/**/*.java
```

### Run Interactive Shell
```bash
java filesystem.Main
```

### Run Demo Mode
```bash
java filesystem.Main --demo
```

### Run Tests
```bash
java filesystem.tests.AllTests
```

---

## Design Rationale

This design prioritizes:

1. **Extensibility**: New commands and strategies can be added without modifying existing code
2. **Testability**: Components are loosely coupled with dependencies injected, making unit testing straightforward
3. **Maintainability**: Clear separation of concerns makes the codebase easy to understand and modify
4. **Reusability**: The Composite pattern allows uniform handling of files and directories
5. **Flexibility**: The Strategy pattern enables runtime switching of listing formats

The architecture follows industry best practices for file system design while remaining simple enough to understand and extend.

