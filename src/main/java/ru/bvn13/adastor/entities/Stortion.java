package ru.bvn13.adastor.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author boykovn at 11.03.2019
 */
@Table(
        indexes = @Index(columnList = "hash")
)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Stored portion of data :)
public class Stortion {

    @Id
    private String uuid;

    @Column
    private LocalDateTime storeDate;

    @Column
    private long size;

    @Column
    private String path;

    @Column
    private String hash;

}
