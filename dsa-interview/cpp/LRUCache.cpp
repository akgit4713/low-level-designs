/**
 * LRU & LFU Cache Implementation in C++
 * 
 * Time Complexity: O(1) for get and put
 * Space Complexity: O(capacity)
 */

#include <iostream>
#include <unordered_map>
#include <list>

// ==================== LRU Cache ====================
class LRUCache {
private:
    int capacity;
    std::list<std::pair<int, int>> cache;  // {key, value}
    std::unordered_map<int, std::list<std::pair<int, int>>::iterator> map;

public:
    LRUCache(int capacity) : capacity(capacity) {}
    
    int get(int key) {
        if (map.find(key) == map.end()) {
            return -1;
        }
        
        // Move to front
        cache.splice(cache.begin(), cache, map[key]);
        return map[key]->second;
    }
    
    void put(int key, int value) {
        if (map.find(key) != map.end()) {
            // Update existing
            map[key]->second = value;
            cache.splice(cache.begin(), cache, map[key]);
        } else {
            if (cache.size() >= capacity) {
                // Evict LRU
                int lruKey = cache.back().first;
                cache.pop_back();
                map.erase(lruKey);
            }
            
            cache.push_front({key, value});
            map[key] = cache.begin();
        }
    }
};

// ==================== LFU Cache ====================
class LFUCache {
private:
    int capacity;
    int minFreq;
    
    // key -> {value, freq}
    std::unordered_map<int, std::pair<int, int>> keyToVal;
    
    // freq -> list of keys with that frequency (most recent at front)
    std::unordered_map<int, std::list<int>> freqToKeys;
    
    // key -> iterator in freqToKeys[freq] list
    std::unordered_map<int, std::list<int>::iterator> keyToIter;
    
    void updateFreq(int key) {
        int freq = keyToVal[key].second;
        
        // Remove from current frequency list
        freqToKeys[freq].erase(keyToIter[key]);
        
        // Update minFreq if necessary
        if (freq == minFreq && freqToKeys[freq].empty()) {
            minFreq++;
        }
        
        // Add to new frequency list
        keyToVal[key].second++;
        int newFreq = keyToVal[key].second;
        freqToKeys[newFreq].push_front(key);
        keyToIter[key] = freqToKeys[newFreq].begin();
    }

public:
    LFUCache(int capacity) : capacity(capacity), minFreq(0) {}
    
    int get(int key) {
        if (keyToVal.find(key) == keyToVal.end()) {
            return -1;
        }
        
        updateFreq(key);
        return keyToVal[key].first;
    }
    
    void put(int key, int value) {
        if (capacity == 0) return;
        
        if (keyToVal.find(key) != keyToVal.end()) {
            keyToVal[key].first = value;
            updateFreq(key);
        } else {
            if (keyToVal.size() >= capacity) {
                // Evict LFU
                int evictKey = freqToKeys[minFreq].back();
                freqToKeys[minFreq].pop_back();
                keyToVal.erase(evictKey);
                keyToIter.erase(evictKey);
            }
            
            keyToVal[key] = {value, 1};
            freqToKeys[1].push_front(key);
            keyToIter[key] = freqToKeys[1].begin();
            minFreq = 1;
        }
    }
};

// ==================== TTL Cache (with expiration) ====================
#include <chrono>

class TTLCache {
private:
    struct CacheEntry {
        int value;
        std::chrono::steady_clock::time_point expiry;
    };
    
    int capacity;
    int ttlMs;  // Time to live in milliseconds
    std::list<std::pair<int, CacheEntry>> cache;
    std::unordered_map<int, std::list<std::pair<int, CacheEntry>>::iterator> map;
    
    bool isExpired(const CacheEntry& entry) {
        return std::chrono::steady_clock::now() > entry.expiry;
    }
    
    void evictExpired() {
        while (!cache.empty() && isExpired(cache.back().second)) {
            map.erase(cache.back().first);
            cache.pop_back();
        }
    }

public:
    TTLCache(int capacity, int ttlMs) : capacity(capacity), ttlMs(ttlMs) {}
    
    int get(int key) {
        evictExpired();
        
        if (map.find(key) == map.end()) {
            return -1;
        }
        
        auto it = map[key];
        if (isExpired(it->second)) {
            cache.erase(it);
            map.erase(key);
            return -1;
        }
        
        // Move to front and refresh TTL
        it->second.expiry = std::chrono::steady_clock::now() + std::chrono::milliseconds(ttlMs);
        cache.splice(cache.begin(), cache, it);
        return it->second.value;
    }
    
    void put(int key, int value) {
        evictExpired();
        
        auto expiry = std::chrono::steady_clock::now() + std::chrono::milliseconds(ttlMs);
        
        if (map.find(key) != map.end()) {
            auto it = map[key];
            it->second = {value, expiry};
            cache.splice(cache.begin(), cache, it);
        } else {
            if (cache.size() >= capacity) {
                map.erase(cache.back().first);
                cache.pop_back();
            }
            
            cache.push_front({key, {value, expiry}});
            map[key] = cache.begin();
        }
    }
};

int main() {
    // LRU Cache demo
    std::cout << "--- LRU Cache Demo ---" << std::endl;
    LRUCache lruCache(2);
    
    lruCache.put(1, 1);
    lruCache.put(2, 2);
    std::cout << "Get 1: " << lruCache.get(1) << std::endl;  // 1
    
    lruCache.put(3, 3);  // Evicts key 2
    std::cout << "Get 2: " << lruCache.get(2) << std::endl;  // -1
    
    lruCache.put(4, 4);  // Evicts key 1
    std::cout << "Get 1: " << lruCache.get(1) << std::endl;  // -1
    std::cout << "Get 3: " << lruCache.get(3) << std::endl;  // 3
    std::cout << "Get 4: " << lruCache.get(4) << std::endl;  // 4
    
    // LFU Cache demo
    std::cout << "\n--- LFU Cache Demo ---" << std::endl;
    LFUCache lfuCache(2);
    
    lfuCache.put(1, 1);
    lfuCache.put(2, 2);
    std::cout << "LFU Get 1: " << lfuCache.get(1) << std::endl;  // 1
    
    lfuCache.put(3, 3);  // Evicts key 2 (LFU)
    std::cout << "LFU Get 2: " << lfuCache.get(2) << std::endl;  // -1
    std::cout << "LFU Get 3: " << lfuCache.get(3) << std::endl;  // 3
    
    return 0;
}
