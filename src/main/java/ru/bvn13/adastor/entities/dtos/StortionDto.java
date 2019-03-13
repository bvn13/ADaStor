package ru.bvn13.adastor.entities.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Transient;
import java.time.LocalDateTime;

/**
 * @author boykovn at 12.03.2019
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StortionDto {

    private String uuid;

    private LocalDateTime storeDate;

    private long size;

    private String path;

    @Transient
    private long retention;

    private String hash;

}
