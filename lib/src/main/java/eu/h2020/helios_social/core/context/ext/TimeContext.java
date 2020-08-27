package eu.h2020.helios_social.core.context.ext;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.sensor.SensorValueListener;

/**
 * This class is a time-based context defined by given start and end times of the time scale
 * when the context is active. The class extends the base class Context.<br/>
 *
 * In order to receive the time updates, this context should be registered as a SensorValueListener
 * for the sensor providing the time updates.
 * For example, time updates from the TimeSensor {@see eu.h2020.helios_social.core.sensor.ext.TimeSensor}:</br>
 * <pre>
 *      TimeContext timecontext1 = new TimeContext("timecontext1", startTime, endTime);
 *      TimeSensor timeSensor = new TimeSensor(1000);  // update interval = 1000 milliseconds
 *      timeSensor.registerValueListener(timecontext1);
 *      timeSensor.startUpdates();
 * </pre>
 * The context active value is updated using the setActive method. The current value of the context
 * can always be checked using the isActive method of the context.</br>
 *
 * If the application needs to track the
 * changes in the active value of the context then the application should implement also
 * the ContextListener interface {@see eu.h2020.helios_social.core.context.ContextListener} and
 * register the context for the listener.
 */
public class TimeContext extends Context implements SensorValueListener {

    long startTime;
    long endTime;
    static final int TIME_CONTEXT_TYPE=2;

    /**
     * Creates a TimeContext
     * @param name the name of the context
     * @param startTime the start moment of the time interval (milliseconds since epoch)
     * @param endTime the end moment of the time interval (milliseconds since epoch)
     */
    public TimeContext(String name, long startTime, long endTime) {
        super(TIME_CONTEXT_TYPE, name, false);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Receive updated time values from the time sensor.
     * For example, from the TimeSensor {@see eu.h2020.helios_social.core.sensor.ext.TimeSensor}.
     * In order to receive the time updates, this context should be registered as a SensorValueListener
     * for the sensor.
     * @param value the received value
     */
    @Override
    public void receiveValue(Object value) {
        long currentTime = (Long) value;
        // if the current time is between the startTime and endTime
        // set the context active (True), otherwise inactive (False)
        setActive(startTime <= currentTime && endTime >= currentTime);
    }
}
