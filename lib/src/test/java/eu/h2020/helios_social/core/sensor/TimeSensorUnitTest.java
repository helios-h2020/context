package eu.h2020.helios_social.core.sensor;

import org.junit.Test;

import java.util.Iterator;

import eu.h2020.helios_social.core.sensor.ext.TimeSensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TimeSensor local unit tests
 *
 * @see TimeSensor
 */
public class TimeSensorUnitTest   {

    @Test
    public void startUpdatesTest() {

        TimeSensor timeSensor = new TimeSensor(2000);

        SensorValueListener listener = new SensorValueListener() {
            @Override
            public void receiveValue(Object value) {
                System.out.println("Value received:" +  ((Long)value / 1000));
            }
        };
        timeSensor.registerValueListener(listener);
        timeSensor.startUpdates();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        timeSensor.startUpdates();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}