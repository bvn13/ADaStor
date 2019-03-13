package ru.bvn13.adastor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author boykovn at 11.03.2019
 */
@Configuration
public class Config {

    @Value("${adastor.storage.path}")
    private String storagePath;

    @Getter
    @Value("${adastor.storage.space.free}")
    private Long freeSpace;

    @Getter
    @Value("${adastor.storage.space.critical}")
    private Long criticalSpace;

    @Getter
    @Value("${adastor.max-size}")
    private Long maxSize;

    @Getter
    @Value("${adastor.min-days-storing}")
    private Long minDaysStoring;

    @Getter
    @Value("${adastor.max-days-storing}")
    private Long maxDaysStoring;

    @PostConstruct
    public void checkParams() {
        if (storagePath == null || storagePath.isEmpty()) {
            throw new IllegalArgumentException("Storage path is not specified!");
        }
        if (freeSpace == null || freeSpace.equals(0L)) {
            throw new IllegalArgumentException("Free space is not specified!");
        }
        if (maxSize == null || maxSize.equals(0L)) {
            throw new IllegalArgumentException("Max size is not specified!");
        }
        if (maxDaysStoring == null || maxDaysStoring.equals(0L)) {
            throw new IllegalArgumentException("Max days storing is not specified!");
        }
        if (criticalSpace == null) {
            throw new IllegalArgumentException("Critical space is not specified!");
        }
        if (criticalSpace.compareTo(freeSpace) > 0) {
            throw new IllegalArgumentException("Critical space must be less than free space!");
        }
    }

    public String getStoragePath() {
        String absStoragePath = storagePath.startsWith(".") ? new File("").getAbsolutePath() + storagePath.substring(1) : storagePath;
        File path = new File(absStoragePath);
        if (!path.exists()) {
            if (!path.mkdirs()) {
                throw new RuntimeException("Could not create storage path "+storagePath+" as "+absStoragePath);
            }
        }
        if (absStoragePath.endsWith("/")) {
            return absStoragePath.substring(0, absStoragePath.length()-2);
        }
        return absStoragePath;
    }
}
