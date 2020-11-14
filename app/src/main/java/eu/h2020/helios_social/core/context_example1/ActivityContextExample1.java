package eu.h2020.helios_social.core.context_example1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.material.snackbar.Snackbar;

import eu.h2020.helios_social.core.context.ext.ActivityContext;
import eu.h2020.helios_social.core.sensor.SensorValueListener;
import eu.h2020.helios_social.core.sensor.ext.ActivitySensor;

/**
 *  An activity-based context example test application.
 *  This example creates contexts for different activity types: "Walking", "Running", "In vehicle" ...
 *  The activity contexts (instances of the class ActivityContext) take the activity type as input.
 *  Further, the example shows how to receive updates to the contexts active value.
 */
public class ActivityContextExample1 extends AppCompatActivity implements SensorValueListener {

    private static final String TAG = ActivityContextExample1.class.getSimpleName();

    // UI Widgets
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;

    private TextView mInVehicleView;
    private TextView mInVehicleText;
    private TextView mOnBicycleView;
    private TextView mOnBicycleText;
    private TextView mOnFootView;
    private TextView mOnFootText;
    private TextView mRunningView;
    private TextView mRunningText;
    private TextView mStillView;
    private TextView mStillText;
    private TextView mTiltingView;
    private TextView mTiltingText;
    private TextView mUnknownView;
    private TextView mUnknownText;
    private TextView mWalkingView;
    private TextView mWalkingText;

    // Tracks the status of the activity updates request
    private Boolean mRequestingActivityUpdates;

    // example activity contexts and sensor
    private ActivityContext mInVehicleContext;
    private ActivityContext mOnBicycleContext;
    private ActivityContext mOnFootContext;
    private ActivityContext mRunningContext;
    private ActivityContext mStillContext;
    private ActivityContext mTiltingContext;
    private ActivityContext mUnknownContext;
    private ActivityContext mWalkingContext;
    private ActivitySensor mActivitySensor;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 11;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Locate the UI widgets.
        mStartUpdatesButton = findViewById(R.id.start_updates_button);
        mStopUpdatesButton = findViewById(R.id.stop_updates_button);

        mInVehicleView = findViewById(R.id.inVehicleView);
        mInVehicleText = findViewById(R.id.inVehicleText);
        mOnBicycleView = findViewById(R.id.onBicycleView);
        mOnBicycleText = findViewById(R.id.onBicycleText);
        mOnFootView = findViewById(R.id.onFootView);
        mOnFootText = findViewById(R.id.onFootText);
        mRunningView = findViewById(R.id.runningView);
        mRunningText = findViewById(R.id.runningText);
        mStillView = findViewById(R.id.stillView);
        mStillText = findViewById(R.id.stillText);
        mTiltingView = findViewById(R.id.tiltingView);
        mTiltingText = findViewById(R.id.tiltingText);
        mUnknownView = findViewById(R.id.unknownView);
        mUnknownText = findViewById(R.id.unknownText);
        mWalkingView = findViewById(R.id.walkingView);
        mWalkingText = findViewById(R.id.walkingText);

        mRequestingActivityUpdates = false;

        // New activity contexts
        mInVehicleContext = new ActivityContext("in vehicle", DetectedActivity.IN_VEHICLE);
        mOnBicycleContext = new ActivityContext("on bicycle", DetectedActivity.ON_BICYCLE);
        mOnFootContext = new ActivityContext("on foot", DetectedActivity.ON_FOOT);
        mRunningContext = new ActivityContext("running", DetectedActivity.RUNNING);
        mStillContext = new ActivityContext("still", DetectedActivity.STILL);
        mTiltingContext = new ActivityContext("tilting", DetectedActivity.TILTING);
        mUnknownContext = new ActivityContext("unknown", DetectedActivity.UNKNOWN);
        mWalkingContext = new ActivityContext("walking", DetectedActivity.WALKING);

        mActivitySensor = new ActivitySensor(this); // , 2000);
        mActivitySensor.registerValueListener(mInVehicleContext);
        mActivitySensor.registerValueListener(mOnBicycleContext);
        mActivitySensor.registerValueListener(mOnFootContext);
        mActivitySensor.registerValueListener(mRunningContext);
        mActivitySensor.registerValueListener(mStillContext);
        mActivitySensor.registerValueListener(mTiltingContext);
        mActivitySensor.registerValueListener(mUnknownContext);
        mActivitySensor.registerValueListener(mWalkingContext);

        mActivitySensor.registerValueListener(this);
    }

    /**
     * This method implements the SensorValueListener interface receiveValue method, which
     * obtains values from the ActivitySensor
     * @param value
     */
    @Override
    public void receiveValue(Object value) {
        // update ui when activity value updated
        updateUI();
    }

    /**
     * Handles the Start Context Updates button
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingActivityUpdates) {
            mRequestingActivityUpdates = true;
            setButtonsEnabledState();
            mActivitySensor.startUpdates();
        }
    }

    /**
     * Handles the Stop Context Updates button, stop location updates
     */
    public void stopUpdatesButtonHandler(View view) {
        mActivitySensor.stopUpdates();
        mRequestingActivityUpdates = false;
        setButtonsEnabledState();
    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        setButtonsEnabledState();
        updateContextUI();
    }

    /**
     * Disables/enables start and stop buttons
     */
    private void setButtonsEnabledState() {
        mStartUpdatesButton.setEnabled(!mRequestingActivityUpdates);
        mStopUpdatesButton.setEnabled(mRequestingActivityUpdates);
    }

    /**
     * Sets values for the UI fields
     */
    private void updateContextUI() {
        mInVehicleView.setBackgroundColor(mInVehicleContext.isActive() ? Color.GREEN : Color.WHITE);
        mInVehicleText.setText(Integer.toString(mInVehicleContext.getConfidence()));
        mOnBicycleView.setBackgroundColor(mOnBicycleContext.isActive() ? Color.GREEN : Color.WHITE);
        mOnBicycleText.setText(Integer.toString(mOnBicycleContext.getConfidence()));
        mOnFootView.setBackgroundColor(mOnFootContext.isActive() ? Color.GREEN : Color.WHITE);
        mOnFootText.setText(Integer.toString(mOnFootContext.getConfidence()));
        mRunningView.setBackgroundColor(mRunningContext.isActive() ? Color.GREEN : Color.WHITE);
        mRunningText.setText(Integer.toString(mRunningContext.getConfidence()));
        mStillView.setBackgroundColor(mStillContext.isActive() ? Color.GREEN : Color.WHITE);
        mStillText.setText(Integer.toString(mStillContext.getConfidence()));
        mTiltingView.setBackgroundColor(mTiltingContext.isActive() ? Color.GREEN : Color.WHITE);
        mTiltingText.setText(Integer.toString(mTiltingContext.getConfidence()));
        mUnknownView.setBackgroundColor(mUnknownContext.isActive() ? Color.GREEN : Color.WHITE);
        mUnknownText.setText(Integer.toString(mUnknownContext.getConfidence()));
        mWalkingView.setBackgroundColor(mWalkingContext.isActive() ? Color.GREEN : Color.WHITE);
        mWalkingText.setText(Integer.toString(mWalkingContext.getConfidence()));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRequestingActivityUpdates) {
            mActivitySensor.startUpdates();
        }
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop updates from activity sensor
        if (mRequestingActivityUpdates) {
            mActivitySensor.stopUpdates();
        }
    }

}
