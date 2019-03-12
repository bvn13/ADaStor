package ru.bvn13.adastor.web.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.bvn13.adastor.config.Config;
import ru.bvn13.adastor.entities.Stortion;
import ru.bvn13.adastor.web.repositories.CustomStortionRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.stream.Stream;

/**
 * @author boykovn at 12.03.2019
 */
@Repository
public class StortionRepositoryImpl implements CustomStortionRepository {

    private Config config;

    @Autowired
    public void setConfig(Config config) {
        this.config = config;
    }


    @PersistenceContext
    private EntityManager entityManager;

    /**
     * RETENTION = min_age + (-max_age + min_age) * pow((file_size / max_size - 1), 3)
     *
     * Implemets the query - selecting all stortions ordered by retention ascending
     *
     * SELECT * FROM STORTION
     * ORDER BY 'retention' ASC
     *
     * @return queried collection
     */
    @Override
    public Stream<Stortion> findAllSortedByRetention() {

        long min_age = config.getMinDaysStoring();
        long max_age = config.getMaxDaysStoring();
        long max_size = config.getMaxSize();

        Query query = entityManager.createQuery("SELECT s FROM Stortion s ORDER BY "
                +" (CAST(:min_age as double) + (- CAST(:max_age as double) + CAST(:min_age as double)) * POWER((CAST(s.size as double) / CAST(:max_size as double) - 1), 3)) ASC");
        query.setParameter("min_age", (double)min_age);
        query.setParameter("max_age", (double)max_age);
        query.setParameter("max_size", max_size);

        return (Stream<Stortion>) query.getResultStream();

    }
}
