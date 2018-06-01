package org.shboland.api.convert;

import org.shboland.persistence.db.hibernate.bean.Car;
import org.shboland.domain.entities.JsonCar;
import org.shboland.api.resource.CarController;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Service
public class CarConverter {
    
    public JsonCar toJson(Car car) {
        JsonCar jsonCar = JsonCar.builder()
                .automatic(car.getAutomatic())
                // @InputJsonField
                .build();
        
        jsonCar.add(linkTo(CarController.class).slash(car.getId()).withSelfRel());
        jsonCar.add(linkTo(CarController.class).slash(car.getId()).slash("/persons").withRel("person"));
            // @InputLink

        return jsonCar;
    }
    
    public Car fromJson(JsonCar jsonCar) {
        return carBuilder(jsonCar).build();
    }

    public Car fromJson(JsonCar jsonCar, long carId) {
        return carBuilder(jsonCar)
                .id(carId)
                .build();
    }

    private Car.CarBuilder carBuilder(JsonCar jsonCar) {

        return Car.builder()
                .automatic(jsonCar.getAutomatic())
                // @InputBeanField
        ;
    }
}