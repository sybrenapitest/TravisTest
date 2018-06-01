package org.shboland.persistence.db.repo;

import org.shboland.persistence.db.hibernate.bean.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long>, CarRepositoryCustom {
}