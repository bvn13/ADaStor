package ru.bvn13.adastor.web.services;

import org.apache.commons.codec.binary.Hex;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bvn13.adastor.config.Config;
import ru.bvn13.adastor.entities.Stortion;
import ru.bvn13.adastor.entities.dtos.StortionDto;
import ru.bvn13.adastor.exceptions.AdastorException;
import ru.bvn13.adastor.exceptions.InternalServerError;
import ru.bvn13.adastor.exceptions.StortionExistByHash;
import ru.bvn13.adastor.exceptions.UploadNotAvailable;
import ru.bvn13.adastor.tasks.DiskFreeSpaceChecker;
import ru.bvn13.adastor.web.repositories.StortionRepository;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author boykovn at 11.03.2019
 */
@Service
public class StortionService {

    private StortionRepository stortionRepository;
    private Config config;
    private ModelMapper modelMapper;
    private DiskFreeSpaceChecker diskFreeSpaceChecker;

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

    @Autowired
    public void setDiskFreeSpaceChecker(DiskFreeSpaceChecker diskFreeSpaceChecker) {
        this.diskFreeSpaceChecker = diskFreeSpaceChecker;
    }

    public Optional<StortionDto> findStortion(String uuid) {
        return stortionRepository.findById(uuid).map(this::convertToDto);
    }

    public String getPath(StortionDto stortion) {
        String storagePath = config.getStoragePath();
        return storagePath + stortion.getPath();
    }

    public InputStream getInputStream(StortionDto stortion) throws FileNotFoundException {
        String path = getPath(stortion);
        InputStream targetStream = new DataInputStream(new FileInputStream(path));
        return targetStream;
    }

    public StortionDto createStortion(long dataLength, InputStream is) throws IOException, AdastorException {
        if (!diskFreeSpaceChecker.checkUploadAvailable(dataLength)) {
            throw new UploadNotAvailable("No space left on device!");
        }

        String uuid = UUID.randomUUID().toString();
        String path = String.format("/%s", uuid);
        String fullPath = String.format("%s/%s", config.getStoragePath(), uuid);

        long bytesCount = 0;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerError("SHA-1 not found, Sorry.", e);
        }
        try(
                DigestInputStream dis = new DigestInputStream(new BufferedInputStream(is), md);
                FileOutputStream fos = new FileOutputStream(fullPath)
        ) {
            byte[] buffer = new byte[128];
            int length = 0;
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                bytesCount += length;
            }
        }

        if (dataLength != bytesCount) {
            throw new InternalServerError("Something went wrong. Sorry.");
        }

        char[] hex = Hex.encodeHex(md.digest());
        String hash = new String(hex);

        Optional<StortionDto> similarByHash = findAnyByHash(hash);
        if (similarByHash.isPresent()) {
            (new File(fullPath)).delete();
            throw new StortionExistByHash(similarByHash.get());
        }

        Stortion stortion = new Stortion();
        stortion.setUuid(uuid);
        stortion.setStoreDate(LocalDateTime.now());
        stortion.setPath(path);
        stortion.setSize(bytesCount);
        stortion.setHash(hash);

        stortionRepository.save(stortion);
        return convertToDto(stortion);
    }

    private StortionDto convertToDto(Stortion stortion) {
        StortionDto stortionDto = modelMapper.map(stortion, StortionDto.class);
        stortionDto.setRetention(computeRetention(stortion));
        return stortionDto;
    }

    public Stream<StortionDto> findAllSortedByRetention() {
        Collection<Stortion> stortions = stortionRepository.findAllSortedByRetention();
        return stortions.stream().map(this::convertToDto);
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

    public void removeStortionByUUID(String uuid) {
        stortionRepository.deleteById(uuid);
    }

    private Optional<StortionDto> findAnyByHash(String hash) {
        return stortionRepository.findFirstByHash(hash).map(this::convertToDto);
    }

}
