package util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author Rich Dionne
 * @project BaMDeviceSetup
 * @package util
 * @date 11/3/2018
 */
public class CountdownTimer implements Runnable {

    private IntegerProperty timeRemainingInSeconds = new SimpleIntegerProperty(0);
    private int delayInSeconds;

    public CountdownTimer(int delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
        timeRemainingInSeconds.setValue(delayInSeconds);
    }

    public IntegerProperty getTimeRemainingInSeconds() {
        return timeRemainingInSeconds;
    }

    public void startTimer() {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (delayInSeconds * 1000);
        while(System.currentTimeMillis() <= endTime) {
            timeRemainingInSeconds.setValue((int)(endTime - System.currentTimeMillis()) / 1000);
            System.out.println(timeRemainingInSeconds.getValue());
        }
    }

    @Override
    public void run() {
        startTimer();

    }
}
