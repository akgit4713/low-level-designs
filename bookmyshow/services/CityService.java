package bookmyshow.services;

import bookmyshow.models.City;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for city management.
 */
public interface CityService {
    City addCity(City city);
    Optional<City> getCity(String cityId);
    Optional<City> getCityByName(String name);
    List<City> getAllCities();
    void updateCity(City city);
    void deleteCity(String cityId);
}



