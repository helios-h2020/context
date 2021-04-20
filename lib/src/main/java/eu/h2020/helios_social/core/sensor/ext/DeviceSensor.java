package eu.h2020.helios_social.core.sensor.ext;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import eu.h2020.helios_social.core.sensor.Sensor;

/**
 *  DeviceSensor - This class provides methods to use Android devices build-in sensors
 *  that measure motion, orientation, and various environmental conditions. This class implements the Android
 *  SensorEventListener interface {@see android.hardware.SensorEventListener} and use the
 *  Android SensorManager class {@see android.hardware.SensorManager}.
 *  For available types {@see https://developer.android.com/guide/topics/sensors/sensors_overview#sensor-availability}

 *  This class extends the base Helios Sensor class.
 *  @see eu.h2020.helios_social.core.sensor.Sensor
 */
public class DeviceSensor extends Sensor implements SensorEventListener {
    private final SensorManager sensorManager;
    private final android.hardware.Sensor sensor;
    private final Handler handler;
    private final int sensorType;
    private int samplingPeriodUs;
    private int maxReportLatencyUs;
    private boolean registered;

    /**
     * Creates a DeviceSensor
     * @param appEnv The application environment
     * @param sensorType The sensor type, which should be supported be the Android platform.
     *                   For available types @see https://developer.android.com/guide/topics/sensors/sensors_overview#sensor-availability
     *                   (TYPE_ACCELEROMETER, TYPE_AMBIENT_TEMPERATURE,TYPE_GRAVITY,TYPE_GYROSCOPE,TYPE_LIGHT,
     *                   TYPE_LINEAR_ACCELERATION,TYPE_MAGNETIC_FIELD,TYPE_ORIENTATION,TYPE_PRESSURE,TYPE_PROXIMITY,
     *                   TYPE_RELATIVE_HUMIDITY,TYPE_ROTATION_VECTOR,TYPE_TEMPERATURE)
     * @param samplingPeriodUs
     * @param maxReportLatencyUs
     * @param handler
     */
    public DeviceSensor(String id, Context appEnv, int sensorType, int samplingPeriodUs, int maxReportLatencyUs, Handler handler) {
        super(id);
        this.sensorManager = (SensorManager) appEnv.getSystemService(Context.SENSOR_SERVICE);
        this.sensor = sensorManager.getDefaultSensor(sensorType);
        this.sensorType = sensorType;
        this.samplingPeriodUs = samplingPeriodUs;
        this.maxReportLatencyUs = maxReportLatencyUs;
        this.handler = handler;
        this.registered = false;
    }

    /**
     * Creates a DeviceSensor
     * @param appEnv
     * @param sensorType
     */
    public DeviceSensor(Context appEnv, int sensorType) {
        this(null, appEnv, sensorType, SensorManager.SENSOR_DELAY_NORMAL, 0, null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        receiveValue(event);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
    }

    /**
     * Is sensor registered?
     * @return Boolean value
     */
    public boolean isRegistered() {
        return (sensor != null && registered);
    }

    /**
     * Has related hardware sensor?
     * @return Boolean value
     */
    public boolean hasSensor() { return sensor != null; }


    @Override
    public void startUpdates() {
        if(sensor != null && !registered) {
            registered = sensorManager.registerListener(this, sensor, samplingPeriodUs, maxReportLatencyUs, handler);
        }
    }

    @Override
    public void stopUpdates() {
        if(isRegistered()) {
            sensorManager.unregisterListener(this);
            registered = false;
        }
    }
}
