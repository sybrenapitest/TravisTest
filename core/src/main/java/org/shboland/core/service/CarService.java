package org.shboland.core.service;

import org.shboland.persistence.db.repo.PersonRepository;
import org.shboland.persistence.db.hibernate.bean.Person;
import java.util.List;
import org.shboland.persistence.criteria.CarSearchCriteria;
import java.util.Optional;
import org.shboland.persistence.db.hibernate.bean.Car;
import org.shboland.persistence.db.repo.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CarService {

    private final CarRepository carRepository;
    private final PersonRepository personRepository;
    // @FieldInput

    @Autowired
    public CarService(PersonRepository personRepository, CarRepository carRepository) {
        this.carRepository = carRepository;
        this.personRepository = personRepository;
        // @ConstructorInput
    }
    
    // @Input

    public List<Car> fetchCarsForPerson(long personId) {
        CarSearchCriteria carSearchCriteria =  CarSearchCriteria.builder()
                .personId(Optional.of(personId))
                .build();

        return carRepository.findBySearchCriteria(carSearchCriteria);
    }

    public boolean removePerson(long carId, long personId) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
         
            if (car.getPerson() != null) {

                Optional<Person> personOptional = personRepository.findById(personId);
                if (personOptional.isPresent() && personOptional.get().getId().equals(car.getPerson().getId())) {
    
                    Car newCar = car.toBuilder()
                            .person(null)
                            .build();
                    carRepository.save(newCar);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean updateCarWithPerson(long carId, long personId) {
        Optional<Car> carOptional = carRepository.findById(carId);
        if (carOptional.isPresent()) {

            Optional<Person> personOptional = personRepository.findById(personId);
            if (personOptional.isPresent()) {

                Car newCar = carOptional.get().toBuilder()
                        .person(personOptional.get())
                        .build();
                carRepository.save(newCar);
                return true;
            }
        }

        return false;
    }
  
    public int findNumberOfCar(CarSearchCriteria sc) {
        return carRepository.findNumberOfCarBySearchCriteria(sc);
    }
    

    public List<Car> findBySearchCriteria(CarSearchCriteria sc) {
        return carRepository.findBySearchCriteria(sc);
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public Optional<Car> fetchCar(long carId) {
        return carRepository.findById(carId);
    }

    public boolean deleteCar(long carId) {
        Optional<Car> car = carRepository.findById(carId);

        if (car.isPresent()) {
            carRepository.delete(car.get());
            return true;
        } else {
            return false;
        }
    }
    
}