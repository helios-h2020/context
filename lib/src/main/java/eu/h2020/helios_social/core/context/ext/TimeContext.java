package eu.h2020.helios_social.core.context.ext;

import java.util.Calendar;

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

    final long startTime;
    final long endTime;
    final int repeat;

    final public static int REPEAT_NONE = 0;
    final public static int REPEAT_DAILY = 1;
    final public static int REPEAT_WEEKLY = 2;
    final public static int REPEAT_WEEKDAYS = 4;
    final public static int REPEAT_WEEKENDS = 8;

    /**
     * Creates a TimeContext
     * @param name the name of the context
     * @param startTime the start moment of the time interval (milliseconds since epoch)
     * @param endTime the end moment of the time interval (milliseconds since epoch)
     */
    public TimeContext(String name, long startTime, long endTime) {
        this(null, name, startTime, endTime, REPEAT_NONE);
    }

    /**
     * Creates a TimeContext
     * @param id the identifier of the context
     * @param name the name of the context
     * @param startTime the start moment of the time interval (milliseconds since epoch)
     * @param endTime the end moment of the time interval (milliseconds since epoch)
     */
    public TimeContext(String id, String name, long startTime, long endTime) {
        this(id, name, startTime, endTime, REPEAT_NONE);
    }

    /**
     * Creates a TimeContext
     * @param id the identifier of the context
     * @param name the name of the context
     * @param startTime the start moment of the time interval (milliseconds since epoch)
     * @param endTime the end moment of the time interval (milliseconds since epoch)
     * @param repeat the repeat interval (REPEAT_NONE,REPEAT_DAILY or REPEAT_WEEKLY)
     */
    public TimeContext(String id, String name, long startTime, long endTime, int repeat) {
        super(id, name, false);
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeat = repeat;
    }

    /**
     * Returns start time
     * @return the start time (milliseconds since epoch)
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Returns end time
     * @return the end time (milliseconds since epoch)
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Returns repeat interval value (REPEAT_NONE, REPEAT_DAILY, REPEAT_WEEKLY
     * @return the repeat interval
     */
    public int getRepeat() {
        return repeat;
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
        switch (repeat) {
            case REPEAT_NONE:
                setActive(startTime <= currentTime && endTime >= currentTime);
                break;
            case REPEAT_DAILY:
                setActive(repeatDaily(currentTime));
                break;
            case REPEAT_WEEKLY:
                int millisWeek = 604800000;
                long dt2 = (currentTime / millisWeek)*millisWeek;
                currentTime -= dt2;
                setActive(startTime - dt2 <= currentTime && endTime - dt2 >= currentTime);
                break;
            case REPEAT_WEEKDAYS:
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(currentTime);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                setActive(!(dayOfWeek == Calendar.SATURDAY | dayOfWeek == Calendar.SUNDAY) && repeatDaily(currentTime));
                break;
            case REPEAT_WEEKENDS:
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(currentTime);
                int day = cal.get(Calendar.DAY_OF_WEEK);
                setActive(!(day == Calendar.MONDAY | day == Calendar.TUESDAY |
                        day == Calendar.WEDNESDAY | day == Calendar.THURSDAY |
                        day == Calendar.FRIDAY) && repeatDaily(currentTime));
                break;
        }
    }

    private boolean repeatDaily(long currentTime) {
        int millisDay = 86400000;
        long dt1 = (currentTime / millisDay)*millisDay;
        currentTime -= dt1;
        return(startTime - dt1  <= currentTime && endTime - dt1 >= currentTime);
    }

}
