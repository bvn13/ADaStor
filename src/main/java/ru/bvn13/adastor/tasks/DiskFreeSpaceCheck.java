package ru.bvn13.adastor.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.bvn13.adastor.config.Config;

import java.io.File;

/**
 * @author boykovn at 12.03.2019
 */
@Component
public class DiskFreeSpaceCheck {

    private Config config;

    @Autowired
    public void setConfig(Config config) {
        this.config = config;
    }

    @Scheduled(fixedDelay = 30000)
    public void checkFreeDiskSpace() {
        double space = getSpaceLeft();
        if (space <= config.getFreeSpace()) {
            removeOldStortions();
        }
    }

    public double getSpaceLeft() {
        File path = new File(config.getStoragePath());
        double space = (double) path.getFreeSpace() / 1024 / 1024;
        return space;
    }

    public void removeOldStortions() {

    }

}
