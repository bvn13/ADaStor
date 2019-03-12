package ru.bvn13.adastor.web.repositories;

import ru.bvn13.adastor.entities.Stortion;

import java.util.stream.Stream;

/**
 * @author boykovn at 12.03.2019
 */
public interface CustomStortionRepository {

    Stream<Stortion> findAllSortedByRetention();

}
