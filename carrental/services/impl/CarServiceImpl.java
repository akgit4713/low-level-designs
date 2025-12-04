package carrental.services.impl;

import carrental.enums.CarStatus;
import carrental.enums.CarType;
import carrental.exceptions.CarNotFoundException;
import carrental.models.Car;
import carrental.models.SearchCriteria;
import carrental.repositories.CarRepository;
import carrental.repositories.ReservationRepository;
import carrental.services.CarService;
import carrental.strategies.search.SearchStrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CarService.
 * Follows SRP - only handles car-related operations.
 * Uses DIP - depends on abstractions (repositories, strategies).
 */
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final SearchStrategy searchStrategy;

    public CarServiceImpl(CarRepository carRepository, 
                          ReservationRepository reservationRepository,
                          SearchStrategy searchStrategy) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
        this.searchStrategy = searchStrategy;
    }

    @Override
    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    @Override
    public Car getCarById(String carId) {
        return carRepository.findById(carId)
            .orElseThrow(() -> new CarNotFoundException(carId));
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public List<Car> getAvailableCars() {
        return carRepository.findAvailable();
    }

    @Override
    public List<Car> getCarsByType(CarType carType) {
        return carRepository.findByType(carType);
    }

    @Override
    public List<Car> searchCars(SearchCriteria criteria) {
        List<Car> allCars = carRepository.findAll();
        List<Car> matchingCars = searchStrategy.search(allCars, criteria);
        
        // If date range specified, filter by availability
        if (criteria.hasDateRange()) {
            return matchingCars.stream()
                .filter(car -> isCarAvailable(car.getId(), criteria.getStartDate(), criteria.getEndDate()))
                .collect(Collectors.toList());
        }
        
        return matchingCars;
    }

    @Override
    public boolean isCarAvailable(String carId, LocalDate startDate, LocalDate endDate) {
        Car car = getCarById(carId);
        
        // Check if car is in available status
        if (!car.isAvailable()) {
            return false;
        }
        
        // Check for conflicting reservations
        return reservationRepository.findActiveByCarIdAndDateRange(carId, startDate, endDate).isEmpty();
    }

    @Override
    public List<Car> getAvailableCarsForDates(LocalDate startDate, LocalDate endDate) {
        return carRepository.findAvailable().stream()
            .filter(car -> isCarAvailable(car.getId(), startDate, endDate))
            .collect(Collectors.toList());
    }

    @Override
    public boolean removeCar(String carId) {
        if (!carRepository.existsById(carId)) {
            throw new CarNotFoundException(carId);
        }
        return carRepository.deleteById(carId);
    }

    @Override
    public void markUnderMaintenance(String carId) {
        Car car = getCarById(carId);
        car.setStatus(CarStatus.UNDER_MAINTENANCE);
        carRepository.save(car);
    }

    @Override
    public void markAvailable(String carId) {
        Car car = getCarById(carId);
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);
    }
}



