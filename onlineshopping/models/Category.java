package onlineshopping.models;

import java.util.*;

/**
 * Represents a product category with hierarchical support
 */
public class Category {
    private final String id;
    private final String name;
    private final String description;
    private final Category parent;
    private final List<Category> subcategories;
    private final Set<String> attributes; // Category-specific attributes

    public Category(String id, String name, String description, Category parent) {
        this.id = Objects.requireNonNull(id, "Category ID is required");
        this.name = Objects.requireNonNull(name, "Category name is required");
        this.description = description;
        this.parent = parent;
        this.subcategories = new ArrayList<>();
        this.attributes = new HashSet<>();
        
        if (parent != null) {
            parent.addSubcategory(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Optional<Category> getParent() {
        return Optional.ofNullable(parent);
    }

    public List<Category> getSubcategories() {
        return Collections.unmodifiableList(subcategories);
    }

    public Set<String> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    public void addSubcategory(Category subcategory) {
        subcategories.add(subcategory);
    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    /**
     * Get the full path from root to this category
     */
    public String getPath() {
        if (parent == null) {
            return name;
        }
        return parent.getPath() + " > " + name;
    }

    /**
     * Check if this category is a descendant of another
     */
    public boolean isDescendantOf(Category ancestor) {
        if (parent == null) {
            return false;
        }
        if (parent.equals(ancestor)) {
            return true;
        }
        return parent.isDescendantOf(ancestor);
    }

    /**
     * Get all categories in the hierarchy (including this one)
     */
    public List<Category> getAllInHierarchy() {
        List<Category> all = new ArrayList<>();
        all.add(this);
        for (Category sub : subcategories) {
            all.addAll(sub.getAllInHierarchy());
        }
        return all;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Category{id='%s', name='%s', path='%s'}", id, name, getPath());
    }
}



