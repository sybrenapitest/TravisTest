package integration;
         
import java.util.HashSet;
import org.shboland.persistence.db.repo.CarRepository;
import org.shboland.persistence.db.hibernate.bean.Car;
import org.shboland.domain.entities.JsonCar;
import org.shboland.persistence.db.repo.PersonRepository;
import org.shboland.persistence.db.hibernate.bean.Person;
import org.shboland.domain.entities.JsonPerson;

public class IntegrationTestFactory {

    // @Input

   public static Car givenACarWithPerson(CarRepository carRepository, PersonRepository personRepository) {
        Person person = personRepository.save(Person.builder()
            .carSet(new HashSet<>())
            .build());

        Car car = carRepository.save(Car.builder()
                .person(person)
                // @FieldInput
                .build());
        
        person.getCarSet().add(car);
        return car;
    }

   public static Person givenAPersonWithCar(PersonRepository personRepository, CarRepository carRepository) {
        Person person = personRepository.save(Person.builder()
            .carSet(new HashSet<>())
            .build());

        Car car = carRepository.save(Car.builder()
                .person(person)
                // @FieldInput
                .build());

        person.getCarSet().add(car);
        return person;
    }

    public static Car givenACar(CarRepository carRepository) {
        return givenACar(Car.builder()
                 .automatic(true)
                // @FieldInputCarBean
                .build(),
                carRepository);
    }
    
    public static Car givenACar(Car car, CarRepository carRepository) {
        return carRepository.save(car);
    }
    
    public static JsonCar givenAJsonCar() {
        return JsonCar.builder()
                 .automatic(false)
                // @FieldInputJsonCar
                .build();
    }
        

    public static Person givenAPerson(PersonRepository personRepository) {
        return givenAPerson(Person.builder()
                 .name("string")
                // @FieldInputPersonBean
                .build(),
                personRepository);
    }
    
    public static Person givenAPerson(Person person, PersonRepository personRepository) {
        return personRepository.save(person);
    }
    
    public static JsonPerson givenAJsonPerson() {
        return JsonPerson.builder()
                 .name("different string")
                // @FieldInputJsonPerson
                .build();
    }
        
}
