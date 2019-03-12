package ru.bvn13.adastor.web.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bvn13.adastor.config.Config;
import ru.bvn13.adastor.entities.Stortion;
import ru.bvn13.adastor.entities.dtos.StortionDto;
import ru.bvn13.adastor.web.repositories.StortionRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author boykovn at 11.03.2019
 */
@Service
public class StortionService {

    private StortionRepository stortionRepository;
    private Config config;
    private ModelMapper modelMapper;

    @Autowired
    public void setStortionRepository(StortionRepository stortionRepository) {
        this.stortionRepository = stortionRepository;
    }

    @Autowired
    public void setConfig(Config config) {
        this.config = config;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
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

    public StortionDto createStortion(InputStream is) throws IOException {
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
        return convertToDto(stortion);
    }

    private StortionDto convertToDto(Stortion stortion) {
        StortionDto stortionDto = modelMapper.map(stortion, StortionDto.class);
        stortionDto.setRetention(computeRetention(stortion));
        return stortionDto;
    }

    public Stream<StortionDto> findAllSortedByRetention() {
        Stream<Stortion> stortions = stortionRepository.findAllSortedByRetention();
        return stortions.map(this::convertToDto);
    }

    /**
     * RETENTION = min_age + (-max_age + min_age) * pow((file_size / max_size - 1), 3)
     * @return retention
     */
    private long computeRetention(Stortion stortion) {
        double retention = config.getMinDaysStoring()
                + (-config.getMaxDaysStoring() + config.getMinDaysStoring()) * Math.pow((double) stortion.getSize() / config.getMaxSize() - 1, 3);
        return Math.round(retention);
    }

}
