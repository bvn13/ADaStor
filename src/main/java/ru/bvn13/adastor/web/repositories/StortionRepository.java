package ru.bvn13.adastor.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bvn13.adastor.entities.Stortion;

import java.util.Optional;

/**
 * @author boykovn at 11.03.2019
 */
@Repository
public interface StortionRepository extends JpaRepository<Stortion, String>, CustomStortionRepository {

    Iterable<Stortion> findAllByHash(String hash);
    Optional<Stortion> findFirstByHash(String hash);

}
