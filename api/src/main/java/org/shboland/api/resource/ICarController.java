package org.shboland.api.resource;

import javax.ws.rs.BeanParam;
import org.shboland.domain.entities.JsonSearchResult;
import org.shboland.domain.entities.JsonCarSearchCriteria;
import java.net.URISyntaxException;
import org.shboland.domain.entities.JsonCar;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/cars")
public interface ICarController {

    // @Input

    @RequestMapping(path = "/{carId}/persons", method = RequestMethod.GET)
    ResponseEntity getPerson(@PathVariable long carId);

    @RequestMapping(value = "/{carId}/persons/{personId}", method = RequestMethod.DELETE)
    ResponseEntity deletePersonWithCar(@PathVariable("carId") long carId, @PathVariable("personId") long personId);

    @RequestMapping(value = "/{carId}/persons/{personId}", method = RequestMethod.PUT)
    ResponseEntity putPersonWithCar(@PathVariable("carId") long carId, @PathVariable("personId") long personId);

    @RequestMapping(path = "/{carId}", method = RequestMethod.GET)
    ResponseEntity<JsonCar> getCar(@PathVariable long carId);
    
    @RequestMapping(path = "", method = RequestMethod.GET)
    ResponseEntity<JsonSearchResult> list(@BeanParam JsonCarSearchCriteria searchCriteria);
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity postCar(@RequestBody JsonCar car) throws URISyntaxException;
    
    @RequestMapping(value = "/{carId}", method = RequestMethod.PUT)
    ResponseEntity putCar(@PathVariable("carId") long carId, @RequestBody JsonCar jsonCar);
    
    @RequestMapping(value = "/{carId}", method = RequestMethod.DELETE)
    ResponseEntity deleteCar(@PathVariable("carId") long carId);
    
}