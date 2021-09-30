package eu.h2020.helios_social.core.context;

import org.junit.Test;

import eu.h2020.helios_social.core.context.ext.TimeContext;
import eu.h2020.helios_social.core.sensor.ext.TimeSensor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TimeContext local unit tests
 *
 * @see TimeContext
 */
public class TimeContextUnitTest {

    @Test
    public void timeContextTest() {
        long startTime = System.currentTimeMillis() + 10000;
        long endTime = startTime + 10000;
        TimeContext meetingContext = new TimeContext("meeting1", "Helios meeting", startTime, endTime);
        TimeSensor timeSensor = new TimeSensor("time_sensor", 1000);
        meetingContext.addSensor(timeSensor);
        timeSensor.startUpdates();
        System.out.println("timeContext: isactive " + meetingContext.isActive());
        assertFalse(meetingContext.isActive());
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("timeContext: isactive " + meetingContext.isActive());
        assertTrue(meetingContext.isActive());
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("timeContext: isactive " + meetingContext.isActive());
        assertFalse(meetingContext.isActive());
    }

}