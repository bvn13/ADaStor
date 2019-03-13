package ru.bvn13.adastor.exceptions;

import lombok.Getter;
import ru.bvn13.adastor.entities.dtos.StortionDto;

/**
 * @author boykovn at 13.03.2019
 */
public class StortionExistByHash extends AdastorException {
    @Getter
    private StortionDto stortion;

    public StortionExistByHash(StortionDto stortionDto) {
        this.stortion = stortionDto;
    }
}
