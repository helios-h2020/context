package eu.h2020.helios_social.core.sensor;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import eu.h2020.helios_social.core.sensor.ext.DeviceSensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DeviceSensorInstrumentedTest {

    @Test
    public void deviceSensorTest() {
        // Application context
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Helios DeviceSensor
        DeviceSensor sensor = new DeviceSensor(appContext, android.hardware.Sensor.TYPE_ACCELEROMETER);

        assertFalse(sensor.isRegistered());
        sensor.startUpdates();
        // normally after startUpdates() , the sensor.isregistered() should return true if the device supports the accelerometer)
        if(sensor.hasSensor()) {
            assertTrue(sensor.isRegistered());
        }
        sensor.stopUpdates();
        assertFalse(sensor.isRegistered());
    }
}
