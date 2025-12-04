package restaurant.services.impl;

import restaurant.enums.MenuCategory;
import restaurant.models.MenuItem;
import restaurant.repositories.impl.InMemoryMenuRepository;
import restaurant.services.MenuService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of MenuService
 */
public class MenuServiceImpl implements MenuService {
    
    private final InMemoryMenuRepository menuRepository;
    
    public MenuServiceImpl(InMemoryMenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }
    
    @Override
    public MenuItem addMenuItem(MenuItem item) {
        return menuRepository.save(item);
    }
    
    @Override
    public Optional<MenuItem> getMenuItem(String itemId) {
        return menuRepository.findById(itemId);
    }
    
    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuRepository.findAll();
    }
    
    @Override
    public List<MenuItem> getMenuByCategory(MenuCategory category) {
        return menuRepository.findByCategory(category);
    }
    
    @Override
    public List<MenuItem> getAvailableMenu() {
        return menuRepository.findAvailable();
    }
    
    @Override
    public List<MenuItem> searchMenu(String query) {
        return menuRepository.searchByName(query);
    }
    
    @Override
    public void updateAvailability(String itemId, boolean available) {
        menuRepository.findById(itemId).ifPresent(item -> item.setAvailable(available));
    }
    
    @Override
    public boolean removeMenuItem(String itemId) {
        return menuRepository.deleteById(itemId);
    }
    
    @Override
    public List<MenuItem> getVegetarianMenu() {
        return menuRepository.findVegetarian();
    }
}

