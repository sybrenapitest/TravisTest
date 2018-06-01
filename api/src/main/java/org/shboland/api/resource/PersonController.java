package org.shboland.api.resource;

import org.shboland.api.convert.CarConverter;
import org.shboland.core.service.CarService;
import org.shboland.domain.entities.JsonCar;
import org.shboland.persistence.db.hibernate.bean.Car;
import org.shboland.persistence.db.hibernate.bean.Person;
import java.util.stream.Collectors;
import javax.ws.rs.BeanParam;
import java.util.List;
import java.util.ArrayList;
import org.shboland.persistence.criteria.PersonSearchCriteria;
import org.shboland.api.convert.ConvertException;
import org.shboland.domain.entities.JsonSearchResult;
import org.shboland.domain.entities.JsonPersonSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.shboland.api.convert.PersonSearchCriteriaConverter;
import java.net.URISyntaxException;
import org.springframework.http.HttpStatus;
import org.shboland.domain.entities.JsonPerson;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.shboland.core.service.PersonService;
import org.shboland.api.convert.PersonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PersonController implements IPersonController {

    private final PersonService personService;
    private final PersonConverter personConverter;
    private final PersonSearchCriteriaConverter personSearchCriteriaConverter;
    private final CarService carService;
    private final CarConverter carConverter;
    // @FieldInput

    @Autowired
    public PersonController(CarConverter carConverter, CarService carService, PersonSearchCriteriaConverter personSearchCriteriaConverter, PersonService personService, PersonConverter personConverter) {
        this.personService = personService;
        this.personConverter = personConverter;
        this.personSearchCriteriaConverter = personSearchCriteriaConverter;
        this.carService = carService;
        this.carConverter = carConverter;
        // @ConstructorInput
    }
    
    // @Input

    @Override
    public ResponseEntity getCars(@PathVariable long personId) {
        List<Car> carList = carService.fetchCarsForPerson(personId);

        JsonSearchResult<JsonCar> result = JsonSearchResult.<JsonCar>builder()
                .results(carList.stream().map(carConverter::toJson).collect(Collectors.toList()))
                .numberOfResults(carList.size())
                .grandTotalNumberOfResults(carList.size())
                .build();
        
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity deleteCarWithPerson(@PathVariable long personId, @PathVariable long carId) {

        return personService.removeCar(personId, carId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity putCarWithPerson(@PathVariable long personId, @PathVariable long carId) {

        return personService.updatePersonWithCar(personId, carId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<JsonPerson> getPerson(@PathVariable long personId) {
        Optional<Person> personOptional = personService.fetchPerson(personId);

        return personOptional.isPresent() ?
                ResponseEntity.ok(personConverter.toJson(personOptional.get())) :
                ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<JsonSearchResult> list(@BeanParam JsonPersonSearchCriteria searchCriteria) {

        PersonSearchCriteria sc;
        try {
            sc = personSearchCriteriaConverter.createSearchCriteria(searchCriteria);
        } catch (ConvertException e) {
            log.warn("Conversion failed!", e);
            return ResponseEntity.badRequest().build();
        }

        List<Person> personList = new ArrayList<>();
        int numberOfPerson = personService.findNumberOfPerson(sc);
        if (numberOfPerson > 0) {
            personList = personService.findBySearchCriteria(sc);
        }

        JsonSearchResult<JsonPerson> result = JsonSearchResult.<JsonPerson>builder()
                .results(personList.stream().map(personConverter::toJson).collect(Collectors.toList()))
                .numberOfResults(personList.size())
                .grandTotalNumberOfResults(numberOfPerson)
                .build();

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity postPerson(@RequestBody JsonPerson jsonPerson) throws URISyntaxException {
            
        Person newPerson = personService.save(personConverter.fromJson(jsonPerson));

        return ResponseEntity.status(HttpStatus.CREATED).body(personConverter.toJson(newPerson));
    }

    @Override
    public ResponseEntity<JsonPerson> putPerson(@PathVariable long personId, @RequestBody JsonPerson jsonPerson) {

        Optional<Person> personOptional = personService.fetchPerson(personId);

        Person savedPerson;
        if (!personOptional.isPresent()) {
            savedPerson = personService.save(personConverter.fromJson(jsonPerson));
        } else {
            savedPerson = personService.save(personConverter.fromJson(jsonPerson, personId));
        }

        return ResponseEntity.ok(personConverter.toJson(savedPerson));
    }

    @Override
    public ResponseEntity deletePerson(@PathVariable long personId) {

        return personService.deletePerson(personId) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }
    
}