package org.shboland.api.convert;

import org.shboland.domain.entities.JsonCarSearchCriteria;
import org.shboland.persistence.criteria.CarSearchCriteria;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("carSearchCriteriaConverter")
public class CarSearchCriteriaConverter {

    public CarSearchCriteria createSearchCriteria(JsonCarSearchCriteria jsonCarSearchCriteria) {
        CarSearchCriteria.CarSearchCriteriaBuilder searchCriteriaBuilder = CarSearchCriteria.builder();

        searchCriteriaBuilder.start(jsonCarSearchCriteria.getStart());
        if (jsonCarSearchCriteria.getMaxResults() > 0) {
            searchCriteriaBuilder.maxResults(jsonCarSearchCriteria.getMaxResults());
        } else {
            throw new ConvertException("Maximum number of results should be a positive number.");
        }

        Long id = jsonCarSearchCriteria.getId();
        searchCriteriaBuilder.id(Optional.ofNullable(id));
        
        Boolean automatic = jsonCarSearchCriteria.getAutomatic();
        searchCriteriaBuilder.automatic(Optional.ofNullable(automatic));
    
        // @Input

        return searchCriteriaBuilder.build();
    }
    
    // @Function input
}