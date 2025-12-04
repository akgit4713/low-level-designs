package bookmyshow.services.impl;

import bookmyshow.exceptions.EntityNotFoundException;
import bookmyshow.models.City;
import bookmyshow.repositories.CityRepository;
import bookmyshow.services.CityService;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CityService.
 */
public class CityServiceImpl implements CityService {
    
    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City addCity(City city) {
        cityRepository.save(city);
        return city;
    }

    @Override
    public Optional<City> getCity(String cityId) {
        return cityRepository.findById(cityId);
    }

    @Override
    public Optional<City> getCityByName(String name) {
        return cityRepository.findByName(name);
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public void updateCity(City city) {
        if (!cityRepository.exists(city.getId())) {
            throw new EntityNotFoundException("City", city.getId());
        }
        cityRepository.save(city);
    }

    @Override
    public void deleteCity(String cityId) {
        if (!cityRepository.exists(cityId)) {
            throw new EntityNotFoundException("City", cityId);
        }
        cityRepository.delete(cityId);
    }
}



