package eu.h2020.helios_social.core.sensor.ext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.h2020.helios_social.core.sensor.Sensor;

/**
 * This class implements a simple time sensor class.
 * It extends the abstract base class sensor.
 * Time is obtained directly from the system, and sensor value listeners
 * are notified with given time interval about the current time.
 */
public class TimeSensor extends Sensor {
    int timeInterval;
    ScheduledExecutorService scheduler;
    boolean running;

    /**
     * Creates a new TimeSensor
     * @param timeInterval the time interval in milliseconds
     */
    public TimeSensor(String id, int timeInterval) {
        super(id);
        this.timeInterval = timeInterval;
        this.scheduler = null;
        this.running = false;
    }

    /**
     * Creates a new TimeSensor
     * @param timeInterval the time interval in milliseconds
     */
    public TimeSensor(int timeInterval) {
        this(null, timeInterval);
    }

    @Override
    public void startUpdates() {
        if(!running || scheduler == null) {
            running = true;
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleWithFixedDelay
                    (new Runnable() {
                        public void run() {
                            receiveValue(System.currentTimeMillis());
                        }
                    }, 0, timeInterval, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stopUpdates() {
        if(scheduler != null) {
            // stops the scheduler thread
            scheduler.shutdownNow();
            running = false;
        }
    }
}
