package carrental.repositories.impl;

import carrental.enums.CarStatus;
import carrental.enums.CarType;
import carrental.models.Car;
import carrental.repositories.CarRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of CarRepository.
 * Uses ConcurrentHashMap for thread safety.
 */
public class InMemoryCarRepository implements CarRepository {
    
    private final Map<String, Car> cars = new ConcurrentHashMap<>();

    @Override
    public Car save(Car car) {
        cars.put(car.getId(), car);
        return car;
    }

    @Override
    public Optional<Car> findById(String id) {
        return Optional.ofNullable(cars.get(id));
    }

    @Override
    public List<Car> findAll() {
        return new ArrayList<>(cars.values());
    }

    @Override
    public boolean deleteById(String id) {
        return cars.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return cars.containsKey(id);
    }

    @Override
    public long count() {
        return cars.size();
    }

    @Override
    public Car findByLicensePlate(String licensePlate) {
        return cars.values().stream()
            .filter(car -> car.getLicensePlate().equals(licensePlate))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Car> findByStatus(CarStatus status) {
        return cars.values().stream()
            .filter(car -> car.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public List<Car> findByType(CarType carType) {
        return cars.values().stream()
            .filter(car -> car.getCarType() == carType)
            .collect(Collectors.toList());
    }

    @Override
    public List<Car> findAvailable() {
        return findByStatus(CarStatus.AVAILABLE);
    }

    @Override
    public List<Car> findByMake(String make) {
        return cars.values().stream()
            .filter(car -> car.getMake().equalsIgnoreCase(make))
            .collect(Collectors.toList());
    }
}



