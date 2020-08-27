package eu.h2020.helios_social.core.context_example1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import eu.h2020.helios_social.core.sensor.SensorValueListener;
import eu.h2020.helios_social.core.sensor.ext.DeviceSensor;

/**
 *  An example test application that demonstrates the usage of the class DeviceSensor, which
 *  class provides methods to use Android devices build-in sensors that measure motion,
 *  orientation, and various environmental conditions.
 *  Further, the example shows how to receive updates from the DeviceSensor.
 *
 *  @see eu.h2020.helios_social.core.sensor.ext.DeviceSensor
 */
public class DeviceSensorExample1 extends AppCompatActivity implements SensorValueListener {

    private static final String TAG = DeviceSensorExample1.class.getSimpleName();

    // UI Widgets
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private TextView mProximityView;
    private TextView mAzimuthView;
    private TextView mPitchView;
    private TextView mRollView;

    // Tracks the status of the updates request
    private Boolean mRequestingUpdates;

    private float mDistance;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    // Access the
    private DeviceSensor mProximity;
    private DeviceSensor mAccelerometer;
    private DeviceSensor mMagnetometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_sensor_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Locate the UI widgets.
        mStartUpdatesButton = findViewById(R.id.start_updates_button);
        mStopUpdatesButton = findViewById(R.id.stop_updates_button);
        mProximityView = findViewById(R.id.proximity_view);
        mAzimuthView = findViewById(R.id.azimuth_view);
        mPitchView = findViewById(R.id.pitch_view);
        mRollView = findViewById(R.id.rollView);

        mRequestingUpdates = false;

        mProximity = new DeviceSensor(this, Sensor.TYPE_PROXIMITY);
        mAccelerometer = new DeviceSensor(this, Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = new DeviceSensor(this, Sensor.TYPE_MAGNETIC_FIELD);

        mProximity.registerValueListener(this);
        mAccelerometer.registerValueListener(this);
        mMagnetometer.registerValueListener(this);
    }

    private static long lastUpdated = 0;

    /**
     * This method implements the SensorValueListener interface receiveValue method, which
     * obtains values from the DeviceSensor (i.e, mProximity, mAccelerometer and mMagnetometer).
     * @param value - a SensorEvent value
     */
    @Override
    public void receiveValue(Object value) {
        SensorEvent event = (SensorEvent)value;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_PROXIMITY:
                mDistance = event.values[0];
                break;
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, mAccelerometerReading,
                        0, mAccelerometerReading.length);

                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, mMagnetometerReading,
                        0, mMagnetometerReading.length);
                break;
            default:
                Log.d(TAG, "Unknown sensor type: " + event.sensor.getType());
        }
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastUpdated > 300) {   // update UI only if enough time has elapsed since last update
            lastUpdated = currentTime;
            updateUI();
        }
    }

    // Updates the UI
    private void updateUI() {
        mProximityView.setText(Float.toString(mDistance));
        // Update rotation matrix
        SensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);
        // Gets oriantationAngles from the rotationMatrix
        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        mAzimuthView.setText(String.format("%.4f",mOrientationAngles[0]));
        mPitchView.setText(String.format("%.4f", mOrientationAngles[1]));
        mRollView.setText(String.format("%.4f",mOrientationAngles[2]));
    }


    /**
     * Handles the Start Context Updates button
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingUpdates) {
            mRequestingUpdates = true;
            setButtonsEnabledState();
            mProximity.startUpdates();
            mAccelerometer.startUpdates();
            mMagnetometer.startUpdates();
        }
    }

    /**
     * Handles the Stop Context Updates button, stop location updates
     */
    public void stopUpdatesButtonHandler(View view) {
        mProximity.stopUpdates();
        mAccelerometer.stopUpdates();
        mMagnetometer.stopUpdates();
        mRequestingUpdates = false;
        setButtonsEnabledState();
    }

    /**
     * Disables/enables start and stop buttons
     */
    private void setButtonsEnabledState() {
        mStartUpdatesButton.setEnabled(!mRequestingUpdates);
        mStopUpdatesButton.setEnabled(mRequestingUpdates);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingUpdates) {
            mProximity.startUpdates();
            mAccelerometer.startUpdates();
            mMagnetometer.startUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop updates
        if (mRequestingUpdates) {
            mProximity.stopUpdates();
            mAccelerometer.stopUpdates();
            mMagnetometer.stopUpdates();
        }
    }
}
