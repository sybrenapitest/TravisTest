package org.shboland.persistence.db.repo;

import org.shboland.persistence.db.hibernate.bean.Car;
import org.shboland.persistence.criteria.CarSearchCriteria;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CarRepositoryImpl extends AbstractHibernateRepository<Car> implements CarRepositoryCustom {

    private static final String ID_PROPERTY = "id";
    private static final String AUTOMATIC_PROPERTY = "automatic";
    private static final String PERSON_PROPERTY = "person";
    // @Property input

    @Override
    protected Class<Car> getDomainClass() {
        return Car.class;
    }

    @Override
    public int findNumberOfCarBySearchCriteria(CarSearchCriteria sc) {
        CriteriaBuilder criteria = getDefaultCriteria();
        CriteriaQuery<Long> criteriaQuery = criteria.createQuery(Long.class);
        Root<Car> root = criteriaQuery.from(getDomainClass());

        List<Predicate> predicates = createPredicates(sc, criteria, root);

        criteriaQuery.select(criteria.count(root)).distinct(true)
                .where(predicates.toArray(new Predicate[predicates.size()]));

        return getEntityManager()
                .createQuery(criteriaQuery)
                .getSingleResult()
                .intValue();
    }

    @Override
    public List<Car> findBySearchCriteria(CarSearchCriteria sc) {
        CriteriaBuilder criteria = getDefaultCriteria();
        CriteriaQuery<Car> criteriaQuery = criteria.createQuery(getDomainClass());
        Root<Car> root = criteriaQuery.from(getDomainClass());

        List<Predicate> predicates = createPredicates(sc, criteria, root);

        criteriaQuery.select(root).distinct(true)
                .where(predicates.toArray(new Predicate[predicates.size()]));

        return getEntityManager()
                .createQuery(criteriaQuery)
                .setFirstResult(sc.getStart())
                .setMaxResults(sc.getMaxResults())
                .getResultList();
    }

    private List<Predicate> createPredicates(CarSearchCriteria sc, CriteriaBuilder criteria, Root<Car> root) {

        List<Predicate> predicates = new ArrayList<>();

        sc.getId().ifPresent(id -> predicates.add(criteria.equal(root.get(ID_PROPERTY), id)));
        
        sc.getAutomatic().ifPresent(automatic -> predicates.add(criteria.equal(root.get(AUTOMATIC_PROPERTY), automatic)));
    
        sc.getPersonId().ifPresent(personId -> predicates.add(criteria.equal(root.get(PERSON_PROPERTY).get(ID_PROPERTY), personId)));
    
        // @Predicate input

        return predicates;
    }
}
