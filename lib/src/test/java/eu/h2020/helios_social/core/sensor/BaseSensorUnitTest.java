package eu.h2020.helios_social.core.sensor;

import org.junit.Test;

import java.util.Iterator;

import eu.h2020.helios_social.core.sensor.Sensor;
import eu.h2020.helios_social.core.sensor.SensorValueListener;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Base Sensor local unit tests
 */
public class BaseSensorUnitTest extends Sensor {

    @Test
    public void sensorRegisterValueListener() {
        BaseSensorUnitTest sensor = new BaseSensorUnitTest();

        SensorValueListener listener = new SensorValueListener() {
            @Override
            public void receiveValue(Object value) {
            }
        };
        sensor.registerValueListener(listener);

        // check if the registered listener can be found
        Iterator<SensorValueListener> listeners = sensor.getValueListeners();
        assertTrue(listeners.hasNext());
        assertEquals(listeners.next(), listener);

        sensor.unregisterValueListener(listener);
        listeners = sensor.getValueListeners();
        // the unregistered listener should not anymore found
        assertFalse(listeners.hasNext());
    }

    @Test
    public void sensorValueListener() {
        BaseSensorUnitTest sensor = new BaseSensorUnitTest();

        final String testValue = "test1";

        SensorValueListener listener = new SensorValueListener() {
            @Override
            public void receiveValue(Object value) {
                assertEquals(testValue, value);
            }
        };

        sensor.registerValueListener(listener);

        sensor.receiveValue(testValue);  // this should call the SensorValueListener's method receiveValue
    }

    @Override
    public void startUpdates() {
    }

    @Override
    public void stopUpdates() {
    }
}