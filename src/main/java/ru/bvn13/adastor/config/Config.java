package ru.bvn13.adastor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
