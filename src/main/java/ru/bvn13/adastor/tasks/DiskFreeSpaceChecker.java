package ru.bvn13.adastor.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.bvn13.adastor.config.Config;
import ru.bvn13.adastor.web.services.StortionService;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author boykovn at 12.03.2019
 */
@Component
public class DiskFreeSpaceChecker {

    private Config config;
    private StortionService stortionService;

    @Autowired
    public void setConfig(Config config) {
        this.config = config;
    }

    @Autowired
    public void setStortionService(StortionService stortionService) {
        this.stortionService = stortionService;
    }

    @Scheduled(fixedDelay = 30000)
    public void checkFreeDiskSpace() {
        double spaceLeft = getSpaceLeft();
        if (spaceLeft <= config.getFreeSpace()) {
            removeOldStortions(spaceLeft);
        }
    }

    private double getSpaceLeft() {
        File path = new File(config.getStoragePath());
        double space = (double) path.getFreeSpace();// / 1024 / 1024;
        return space;
    }

    private void removeOldStortions(final double currentSpaceLeft) {
        final double mustFreeSpace = config.getFreeSpace();
        final AtomicReference<Double> spaceLeft = new AtomicReference<>(currentSpaceLeft);

        final ExecutorService es = Executors.newFixedThreadPool(10);

        stortionService.findAllSortedByRetention().forEach(stortionDto -> {
            double virtualSpace = spaceLeft.accumulateAndGet((double) stortionDto.getSize(), (a, b) -> a + b);
            if (virtualSpace <= mustFreeSpace) {
                es.submit(() -> {
                    File file = new File(String.format("%s%s", config.getStoragePath(), stortionDto.getPath()));
                    if (file.exists()) {
                        file.delete();
                    }
                    stortionService.removeStortionByUUID(stortionDto.getUuid());
                });
            }
        });

        es.shutdown();
    }

    public boolean checkUploadAvailable(long dataLength) {
        return !(getSpaceLeft() - dataLength <= config.getCriticalSpace());
    }

}
