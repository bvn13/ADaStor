package ru.bvn13.adastor.web.repositories;

import ru.bvn13.adastor.entities.Stortion;

import java.util.Collection;

/**
 * @author boykovn at 12.03.2019
 */
public interface CustomStortionRepository {

    Collection<Stortion> findAllSortedByRetention();

}
