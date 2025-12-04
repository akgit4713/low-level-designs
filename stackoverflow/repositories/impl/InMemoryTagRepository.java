package stackoverflow.repositories.impl;

import stackoverflow.models.Tag;
import stackoverflow.repositories.TagRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of TagRepository.
 */
public class InMemoryTagRepository implements TagRepository {
    private final Map<String, Tag> tags = new ConcurrentHashMap<>();

    @Override
    public Tag save(Tag tag) {
        tags.put(tag.getId(), tag);
        return tag;
    }

    @Override
    public Optional<Tag> findById(String id) {
        return Optional.ofNullable(tags.get(id));
    }

    @Override
    public List<Tag> findAll() {
        return new ArrayList<>(tags.values());
    }

    @Override
    public void delete(String id) {
        tags.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return tags.containsKey(id);
    }

    @Override
    public long count() {
        return tags.size();
    }

    @Override
    public Optional<Tag> findByName(String name) {
        String normalizedName = name.toLowerCase().trim();
        return tags.values().stream()
                .filter(t -> t.getName().equals(normalizedName))
                .findFirst();
    }

    @Override
    public Tag getOrCreate(String name) {
        String normalizedName = name.toLowerCase().trim();
        return findByName(normalizedName).orElseGet(() -> {
            Tag newTag = new Tag(normalizedName);
            return save(newTag);
        });
    }
}



