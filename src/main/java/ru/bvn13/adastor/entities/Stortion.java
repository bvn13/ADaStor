package ru.bvn13.adastor.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author boykovn at 11.03.2019
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Stortion {

    @Id
    private String uuid;

    @Column
    private LocalDateTime storeDate;

    @Column
    private long size;

    @Column
    private String path;

}
