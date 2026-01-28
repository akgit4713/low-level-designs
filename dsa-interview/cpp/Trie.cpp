/**
 * Trie (Prefix Tree) Implementation in C++
 * 
 * Time Complexity:
 * - Insert: O(m) where m is the length of the word
 * - Search: O(m)
 * - StartsWith: O(m)
 * - Delete: O(m)
 * 
 * Space Complexity: O(ALPHABET_SIZE * m * n) where n is number of words
 */

#include <iostream>
#include <string>
#include <vector>
#include <memory>

class Trie {
private:
    static const int ALPHABET_SIZE = 26;
    
    struct TrieNode {
        std::unique_ptr<TrieNode> children[ALPHABET_SIZE];
        bool isEndOfWord;
        int prefixCount;
        int wordCount;
        
        TrieNode() : isEndOfWord(false), prefixCount(0), wordCount(0) {
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                children[i] = nullptr;
            }
        }
    };
    
    std::unique_ptr<TrieNode> root;
    
    TrieNode* searchNode(const std::string& word) {
        TrieNode* current = root.get();
        for (char c : word) {
            int index = c - 'a';
            if (!current->children[index]) {
                return nullptr;
            }
            current = current->children[index].get();
        }
        return current;
    }
    
    bool isEmpty(TrieNode* node) {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (node->children[i]) return false;
        }
        return true;
    }
    
    bool deleteHelper(TrieNode* current, const std::string& word, int index) {
        if (index == word.length()) {
            if (!current->isEndOfWord) return false;
            current->isEndOfWord = false;
            current->wordCount--;
            return isEmpty(current);
        }
        
        int charIndex = word[index] - 'a';
        if (!current->children[charIndex]) return false;
        
        current->children[charIndex]->prefixCount--;
        bool shouldDeleteChild = deleteHelper(current->children[charIndex].get(), word, index + 1);
        
        if (shouldDeleteChild) {
            current->children[charIndex].reset();
            return isEmpty(current) && !current->isEndOfWord;
        }
        return false;
    }
    
    bool searchWithWildcardHelper(TrieNode* node, const std::string& word, int index) {
        if (index == word.length()) {
            return node->isEndOfWord;
        }
        
        char c = word[index];
        if (c == '.') {
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                if (node->children[i] && 
                    searchWithWildcardHelper(node->children[i].get(), word, index + 1)) {
                    return true;
                }
            }
            return false;
        } else {
            int charIndex = c - 'a';
            if (!node->children[charIndex]) return false;
            return searchWithWildcardHelper(node->children[charIndex].get(), word, index + 1);
        }
    }
    
    void collectWords(TrieNode* node, std::string& prefix, std::vector<std::string>& result) {
        if (node->isEndOfWord) {
            result.push_back(prefix);
        }
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (node->children[i]) {
                prefix.push_back('a' + i);
                collectWords(node->children[i].get(), prefix, result);
                prefix.pop_back();
            }
        }
    }

public:
    Trie() : root(std::make_unique<TrieNode>()) {}
    
    void insert(const std::string& word) {
        if (word.empty()) return;
        
        TrieNode* current = root.get();
        for (char c : word) {
            int index = c - 'a';
            if (!current->children[index]) {
                current->children[index] = std::make_unique<TrieNode>();
            }
            current = current->children[index].get();
            current->prefixCount++;
        }
        current->isEndOfWord = true;
        current->wordCount++;
    }
    
    bool search(const std::string& word) {
        TrieNode* node = searchNode(word);
        return node != nullptr && node->isEndOfWord;
    }
    
    bool startsWith(const std::string& prefix) {
        return searchNode(prefix) != nullptr;
    }
    
    int countWordsWithPrefix(const std::string& prefix) {
        TrieNode* node = searchNode(prefix);
        return node ? node->prefixCount : 0;
    }
    
    int countExactWord(const std::string& word) {
        TrieNode* node = searchNode(word);
        return node ? node->wordCount : 0;
    }
    
    bool deleteWord(const std::string& word) {
        if (!search(word)) return false;
        deleteHelper(root.get(), word, 0);
        return true;
    }
    
    bool searchWithWildcard(const std::string& word) {
        return searchWithWildcardHelper(root.get(), word, 0);
    }
    
    std::vector<std::string> getWordsWithPrefix(const std::string& prefix) {
        std::vector<std::string> result;
        TrieNode* node = searchNode(prefix);
        if (node) {
            std::string current = prefix;
            collectWords(node, current, result);
        }
        return result;
    }
};

// ==================== XOR Trie for Maximum XOR Problems ====================
class XORTrie {
private:
    static const int MAX_BITS = 31;
    
    struct TrieNode {
        std::unique_ptr<TrieNode> children[2];
        int count;
        
        TrieNode() : count(0) {
            children[0] = nullptr;
            children[1] = nullptr;
        }
    };
    
    std::unique_ptr<TrieNode> root;

public:
    XORTrie() : root(std::make_unique<TrieNode>()) {}
    
    void insert(int num) {
        TrieNode* current = root.get();
        for (int i = MAX_BITS; i >= 0; i--) {
            int bit = (num >> i) & 1;
            if (!current->children[bit]) {
                current->children[bit] = std::make_unique<TrieNode>();
            }
            current = current->children[bit].get();
            current->count++;
        }
    }
    
    void remove(int num) {
        TrieNode* current = root.get();
        for (int i = MAX_BITS; i >= 0; i--) {
            int bit = (num >> i) & 1;
            if (current->children[bit]) {
                current = current->children[bit].get();
                current->count--;
            }
        }
    }
    
    // Find maximum XOR with given number
    int getMaxXOR(int num) {
        TrieNode* current = root.get();
        int maxXor = 0;
        
        for (int i = MAX_BITS; i >= 0; i--) {
            int bit = (num >> i) & 1;
            int oppositeBit = 1 - bit;
            
            // Try to go opposite direction for max XOR
            if (current->children[oppositeBit] && current->children[oppositeBit]->count > 0) {
                maxXor |= (1 << i);
                current = current->children[oppositeBit].get();
            } else if (current->children[bit] && current->children[bit]->count > 0) {
                current = current->children[bit].get();
            } else {
                break;
            }
        }
        return maxXor;
    }
};

int main() {
    Trie trie;
    
    // Basic operations
    trie.insert("apple");
    trie.insert("app");
    trie.insert("application");
    trie.insert("apply");
    
    std::cout << "Search 'apple': " << (trie.search("apple") ? "true" : "false") << std::endl;
    std::cout << "Search 'app': " << (trie.search("app") ? "true" : "false") << std::endl;
    std::cout << "Search 'appl': " << (trie.search("appl") ? "true" : "false") << std::endl;
    std::cout << "StartsWith 'app': " << (trie.startsWith("app") ? "true" : "false") << std::endl;
    std::cout << "Words with prefix 'app': " << trie.countWordsWithPrefix("app") << std::endl;
    
    std::cout << "All words with prefix 'app': ";
    for (const auto& word : trie.getWordsWithPrefix("app")) {
        std::cout << word << " ";
    }
    std::cout << std::endl;
    
    // XOR Trie demo
    XORTrie xorTrie;
    std::vector<int> nums = {3, 10, 5, 25, 2, 8};
    for (int num : nums) {
        xorTrie.insert(num);
    }
    
    int maxXor = 0;
    for (int num : nums) {
        maxXor = std::max(maxXor, xorTrie.getMaxXOR(num));
    }
    std::cout << "Maximum XOR in array: " << maxXor << std::endl; // Should be 28 (5 XOR 25)
    
    return 0;
}
