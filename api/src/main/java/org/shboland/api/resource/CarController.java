package org.shboland.api.resource;

import org.shboland.api.convert.PersonConverter;
import org.shboland.core.service.PersonService;
import org.shboland.persistence.db.hibernate.bean.Person;
import org.shboland.persistence.db.hibernate.bean.Car;
import java.util.stream.Collectors;
import javax.ws.rs.BeanParam;
import java.util.List;
import java.util.ArrayList;
import org.shboland.persistence.criteria.CarSearchCriteria;
import org.shboland.api.convert.ConvertException;
import org.shboland.domain.entities.JsonSearchResult;
import org.shboland.domain.entities.JsonCarSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.shboland.api.convert.CarSearchCriteriaConverter;
import java.net.URISyntaxException;
import org.springframework.http.HttpStatus;
import org.shboland.domain.entities.JsonCar;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.shboland.core.service.CarService;
import org.shboland.api.convert.CarConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CarController implements ICarController {

    private final CarService carService;
    private final CarConverter carConverter;
    private final CarSearchCriteriaConverter carSearchCriteriaConverter;
    private final PersonService personService;
    private final PersonConverter personConverter;
    // @FieldInput

    @Autowired
    public CarController(PersonConverter personConverter, PersonService personService, CarSearchCriteriaConverter carSearchCriteriaConverter, CarService carService, CarConverter carConverter) {
        this.carService = carService;
        this.carConverter = carConverter;
        this.carSearchCriteriaConverter = carSearchCriteriaConverter;
        this.personService = personService;
        this.personConverter = personConverter;
        // @ConstructorInput
    }
    
    // @Input

    @Override
    public ResponseEntity getPerson(@PathVariable long carId) {
        Person person = personService.fetchPersonForCar(carId);

        return person != null ? 
                    ResponseEntity.ok(personConverter.toJson(person)) : 
                    ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity deletePersonWithCar(@PathVariable long carId, @PathVariable long personId) {

        return carService.removePerson(carId, personId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity putPersonWithCar(@PathVariable long carId, @PathVariable long personId) {

        return carService.updateCarWithPerson(carId, personId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<JsonCar> getCar(@PathVariable long carId) {
        Optional<Car> carOptional = carService.fetchCar(carId);

        return carOptional.isPresent() ?
                ResponseEntity.ok(carConverter.toJson(carOptional.get())) :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<JsonSearchResult> list(@BeanParam JsonCarSearchCriteria searchCriteria) {

        CarSearchCriteria sc;
        try {
            sc = carSearchCriteriaConverter.createSearchCriteria(searchCriteria);
        } catch (ConvertException e) {
            log.warn("Conversion failed!", e);
            return ResponseEntity.badRequest().build();
        }

        List<Car> carList = new ArrayList<>();
        int numberOfCar = carService.findNumberOfCar(sc);
        if (numberOfCar > 0) {
            carList = carService.findBySearchCriteria(sc);
        }

        JsonSearchResult<JsonCar> result = JsonSearchResult.<JsonCar>builder()
                .results(carList.stream().map(carConverter::toJson).collect(Collectors.toList()))
                .numberOfResults(carList.size())
                .grandTotalNumberOfResults(numberOfCar)
                .build();

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity postCar(@RequestBody JsonCar jsonCar) throws URISyntaxException {
            
        Car newCar = carService.save(carConverter.fromJson(jsonCar));

        return ResponseEntity.status(HttpStatus.CREATED).body(carConverter.toJson(newCar));
    }

    @Override
    public ResponseEntity<JsonCar> putCar(@PathVariable long carId, @RequestBody JsonCar jsonCar) {

        Optional<Car> carOptional = carService.fetchCar(carId);

        Car savedCar;
        if (!carOptional.isPresent()) {
            savedCar = carService.save(carConverter.fromJson(jsonCar));
        } else {
            savedCar = carService.save(carConverter.fromJson(jsonCar, carId));
        }

        return ResponseEntity.ok(carConverter.toJson(savedCar));
    }

    @Override
    public ResponseEntity deleteCar(@PathVariable long carId) {

        return carService.deleteCar(carId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }
    
}