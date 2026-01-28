package java;

/**
 * Trie (Prefix Tree) Implementation
 * 
 * Time Complexity:
 * - Insert: O(m) where m is the length of the word
 * - Search: O(m)
 * - StartsWith: O(m)
 * - Delete: O(m)
 * 
 * Space Complexity: O(ALPHABET_SIZE * m * n) where n is number of words
 * 
 * Common Interview Problems:
 * - Autocomplete/Search Suggestions
 * - Word Search II (Leetcode 212)
 * - Implement Trie (Leetcode 208)
 * - Word Dictionary with wildcards (Leetcode 211)
 * - Maximum XOR of Two Numbers (Leetcode 421)
 */
public class Trie {
    
    private static final int ALPHABET_SIZE = 26;
    
    private class TrieNode {
        TrieNode[] children;
        boolean isEndOfWord;
        int prefixCount;  // Count of words with this prefix
        int wordCount;    // Count of this exact word (for duplicates)
        
        TrieNode() {
            children = new TrieNode[ALPHABET_SIZE];
            isEndOfWord = false;
            prefixCount = 0;
            wordCount = 0;
        }
    }
    
    private final TrieNode root;
    
    public Trie() {
        root = new TrieNode();
    }
    
    /**
     * Inserts a word into the trie.
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) return;
        
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
            current.prefixCount++;
        }
        current.isEndOfWord = true;
        current.wordCount++;
    }
    
    /**
     * Returns true if the word is in the trie.
     */
    public boolean search(String word) {
        TrieNode node = searchNode(word);
        return node != null && node.isEndOfWord;
    }
    
    /**
     * Returns true if there is any word in the trie that starts with the given prefix.
     */
    public boolean startsWith(String prefix) {
        return searchNode(prefix) != null;
    }
    
    /**
     * Returns count of words with given prefix.
     */
    public int countWordsWithPrefix(String prefix) {
        TrieNode node = searchNode(prefix);
        return node == null ? 0 : node.prefixCount;
    }
    
    /**
     * Returns count of exact word occurrences.
     */
    public int countExactWord(String word) {
        TrieNode node = searchNode(word);
        return node == null ? 0 : node.wordCount;
    }
    
    /**
     * Deletes a word from the trie.
     */
    public boolean delete(String word) {
        if (!search(word)) return false;
        deleteHelper(root, word, 0);
        return true;
    }
    
    private boolean deleteHelper(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord) return false;
            current.isEndOfWord = false;
            current.wordCount--;
            return isEmpty(current);
        }
        
        int charIndex = word.charAt(index) - 'a';
        TrieNode child = current.children[charIndex];
        if (child == null) return false;
        
        child.prefixCount--;
        boolean shouldDeleteChild = deleteHelper(child, word, index + 1);
        
        if (shouldDeleteChild) {
            current.children[charIndex] = null;
            return isEmpty(current) && !current.isEndOfWord;
        }
        return false;
    }
    
    private boolean isEmpty(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) return false;
        }
        return true;
    }
    
    private TrieNode searchNode(String word) {
        if (word == null) return null;
        
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                return null;
            }
            current = current.children[index];
        }
        return current;
    }
    
    /**
     * Search with wildcard '.' that can match any character.
     * Used in: Design Add and Search Words Data Structure (Leetcode 211)
     */
    public boolean searchWithWildcard(String word) {
        return searchWithWildcardHelper(root, word, 0);
    }
    
    private boolean searchWithWildcardHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            return node.isEndOfWord;
        }
        
        char c = word.charAt(index);
        if (c == '.') {
            // Try all possible characters
            for (TrieNode child : node.children) {
                if (child != null && searchWithWildcardHelper(child, word, index + 1)) {
                    return true;
                }
            }
            return false;
        } else {
            int charIndex = c - 'a';
            if (node.children[charIndex] == null) {
                return false;
            }
            return searchWithWildcardHelper(node.children[charIndex], word, index + 1);
        }
    }
    
    /**
     * Get all words with given prefix (for autocomplete).
     */
    public java.util.List<String> getWordsWithPrefix(String prefix) {
        java.util.List<String> result = new java.util.ArrayList<>();
        TrieNode node = searchNode(prefix);
        if (node != null) {
            collectWords(node, new StringBuilder(prefix), result);
        }
        return result;
    }
    
    private void collectWords(TrieNode node, StringBuilder prefix, java.util.List<String> result) {
        if (node.isEndOfWord) {
            result.add(prefix.toString());
        }
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (node.children[i] != null) {
                prefix.append((char) ('a' + i));
                collectWords(node.children[i], prefix, result);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }
    
    // ==================== DEMO ====================
    public static void main(String[] args) {
        Trie trie = new Trie();
        
        // Basic operations
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        trie.insert("apply");
        
        System.out.println("Search 'apple': " + trie.search("apple"));       // true
        System.out.println("Search 'app': " + trie.search("app"));           // true
        System.out.println("Search 'appl': " + trie.search("appl"));         // false
        System.out.println("StartsWith 'app': " + trie.startsWith("app"));   // true
        System.out.println("Words with prefix 'app': " + trie.countWordsWithPrefix("app")); // 4
        
        System.out.println("All words with prefix 'app': " + trie.getWordsWithPrefix("app"));
        
        // Wildcard search
        trie.insert("bad");
        trie.insert("dad");
        trie.insert("mad");
        System.out.println("Search '.ad': " + trie.searchWithWildcard(".ad")); // true
        System.out.println("Search 'b..': " + trie.searchWithWildcard("b..")); // true
        
        // Delete
        trie.delete("apple");
        System.out.println("After delete 'apple', search 'apple': " + trie.search("apple")); // false
        System.out.println("After delete 'apple', search 'app': " + trie.search("app"));     // true
    }
}
