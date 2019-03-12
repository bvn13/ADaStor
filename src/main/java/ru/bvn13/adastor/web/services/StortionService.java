package ru.bvn13.adastor.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bvn13.adastor.config.Config;
import ru.bvn13.adastor.entities.Stortion;
import ru.bvn13.adastor.web.repositories.StortionRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * @author boykovn at 11.03.2019
 */
@Service
public class StortionService {

    private StortionRepository stortionRepository;
    private Config config;

    @Autowired
    public void setStortionRepository(StortionRepository stortionRepository) {
        this.stortionRepository = stortionRepository;
    }

    @Autowired
    public void setConfig(Config config) {
        this.config = config;
    }

    public Optional<Stortion> findStortion(String uuid) {
        return stortionRepository.findById(uuid);
    }

    public String getPath(Stortion stortion) {
        String storagePath = config.getStoragePath();
        return storagePath + stortion.getPath();
    }

    public InputStream getInputStream(Stortion stortion) throws FileNotFoundException {
        String path = getPath(stortion);
        InputStream targetStream = new DataInputStream(new FileInputStream(path));
        return targetStream;
    }

    public Stortion createStortion(InputStream is) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String path = String.format("/%s", uuid);
        String fullPath = String.format("%s/%s", config.getStoragePath(), uuid);
        Stortion stortion = new Stortion();
        stortion.setUuid(uuid);
        stortion.setStoreDate(LocalDateTime.now());
        stortion.setPath(path);

        long bytesCount = 0;
        try(FileOutputStream fos = new FileOutputStream(fullPath)) {
            bytesCount = is.transferTo(fos);
        }

        stortion.setSize(bytesCount);

        stortionRepository.save(stortion);
        return stortion;
    }

}
