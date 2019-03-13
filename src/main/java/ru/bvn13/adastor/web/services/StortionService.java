package ru.bvn13.adastor.web.services;

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
import java.util.Formatter;
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

    public StortionDto createStortion(long dataLength, InputStream is) throws IOException, AdastorException {
        if (!diskFreeSpaceChecker.checkUploadAvailable(dataLength)) {
            throw new UploadNotAvailable("No space left on device!");
        }

        String uuid = UUID.randomUUID().toString();
        String path = String.format("/%s", uuid);
        String fullPath = String.format("%s/%s", config.getStoragePath(), uuid);

        long bytesCount;
        String hash;
        try(DigestInputStream dis = new DigestInputStream(new BufferedInputStream(is), MessageDigest.getInstance("SHA-1")); FileOutputStream fos = new FileOutputStream(fullPath)) {
            bytesCount = is.transferTo(fos);
            hash = formatMessageDigestToHex(dis);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerError("SHA-1 not found, Sorry.", e);
        }

        Optional<StortionDto> similarByHash = findAnyByHash(hash);
        if (similarByHash.isPresent()) {
            throw new StortionExistByHash(similarByHash.get());
        }

        Stortion stortion = new Stortion();
        stortion.setUuid(uuid);
        stortion.setStoreDate(LocalDateTime.now());
        stortion.setPath(path);
        stortion.setSize(bytesCount);

        stortionRepository.save(stortion);
        return convertToDto(stortion);
    }

    private String formatMessageDigestToHex(DigestInputStream dis) {
        final MessageDigest md = dis.getMessageDigest();
        final byte[] digest = md.digest();

        // Format as HEX
        try (Formatter formatter = new Formatter()) {
            for (final byte b : digest) {
                formatter.format("%02x", b);
            }

            final String sha1 = formatter.toString();
            return sha1;
        }

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

    public void removeStortionByUUID(String uuid) {
        stortionRepository.deleteById(uuid);
    }

    private Iterable<Stortion> findAllByHash(String hash) {
        return stortionRepository.findAllByHash(hash);
    }

    private Optional<StortionDto> findAnyByHash(String hash) {
        return stortionRepository.findFirstByHash(hash).map(this::convertToDto);
    }

}
