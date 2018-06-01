package org.shboland.persistence.db.repo;

import org.shboland.persistence.db.hibernate.bean.Car;
import org.shboland.persistence.criteria.CarSearchCriteria;

import java.util.List;

public interface CarRepositoryCustom {

    int findNumberOfCarBySearchCriteria(CarSearchCriteria sc);

    List<Car> findBySearchCriteria(CarSearchCriteria sc);
}
