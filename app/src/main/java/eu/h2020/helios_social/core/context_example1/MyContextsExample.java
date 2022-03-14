package eu.h2020.helios_social.core.context_example1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.context.ContextAnd;
import eu.h2020.helios_social.core.context.ContextNot;
import eu.h2020.helios_social.core.context.ext.ActivityContext;
import eu.h2020.helios_social.core.context.ext.LocationContext;
import eu.h2020.helios_social.core.profile.HeliosUserData;
import eu.h2020.helios_social.core.sensor.ext.ActivitySensor;
import eu.h2020.helios_social.core.sensor.ext.LocationSensor;

/**
 *  "My contexts" example.
 *  For example, creates location-based contexts and activity contexts
 *  to show their status in a view.
 *
 *  See context and sensor type implementations:
 *  @see LocationContext
 *  @see LocationSensor
 *  @see ActivityContext
 *  @see ActivitySensor
 */
public class MyContextsExample extends AppCompatActivity {

    private static final String TAG = MyContextsExample.class.getSimpleName();

    // Code used in requesting runtime permissions
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final boolean runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

    // UI Widgets
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;

    // Tracks the status of the location updates request
    private Boolean mRequestingLocationUpdates;
    // Tracks the status of the activity updates request
    private Boolean mRequestingActivityUpdates;

    // example contexts
    LocationContext locationContext1;
    LocationContext locationContext2;
    ActivityContext mInVehicleContext;
    ActivityContext mOnBicycleContext;
    ActivityContext mOnFootContext;
    ActivityContext mRunningContext;
    ActivityContext mStillContext;
    ActivityContext mTiltingContext;
    ActivityContext mUnknownContext;
    ActivityContext mWalkingContext;
    ContextNot mMovingContext;
    ContextAnd mMoving2Context;

    // Access the location sensor
    private LocationSensor mLocationSensor;

    private ActivitySensor mActivitySensor;

    public static ArrayList<Context> mMyContexts;

    static String activityPermission = runningQOrLater ? Manifest.permission.ACTIVITY_RECOGNITION :
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION";
    static String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, activityPermission};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_context_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Locate the UI widgets.
        mStartUpdatesButton = findViewById(R.id.start_updates_button);
        mStopUpdatesButton = findViewById(R.id.stop_updates_button);

        mRequestingLocationUpdates = false;
        mRequestingActivityUpdates = false;

        // some tests: create user profile including the home and work location info
        HeliosUserData profile = HeliosUserData.getInstance();
        profile.setValue("home_lat", "60.1805");
        profile.setValue("home_lon", "24.8255");
        profile.setValue("work_lat", "60.1852");
        profile.setValue("work_lon", "24.8172");
        // retrieve home and work coordinates from the profile
        double home_lat = Double.parseDouble(profile.getValue("home_lat"));
        double home_lon = Double.parseDouble(profile.getValue("home_lon"));
        double work_lat = Double.parseDouble(profile.getValue("work_lat"));
        double work_lon = Double.parseDouble(profile.getValue("work_lon"));

        // Create location-based context named "At home".
        // The context is an instance of the LocationContext class
        locationContext1 = new LocationContext("At home", home_lat, home_lon, 1000.0);
        // Register listener to obtain changes in the context active value
        // Create an other context "At work"
        locationContext2 = new LocationContext("At work", work_lat, work_lon, 1000.0);

        // Init LocationSensor
        mLocationSensor = new LocationSensor(this);
        // Register location listeners for the contexts
        mLocationSensor.registerValueListener(locationContext1);
        mLocationSensor.registerValueListener(locationContext2);

        // New activity contexts
        mInVehicleContext = new ActivityContext("In vehicle", DetectedActivity.IN_VEHICLE);
        mOnBicycleContext = new ActivityContext("On bicycle", DetectedActivity.ON_BICYCLE);
        mOnFootContext = new ActivityContext("On foot", DetectedActivity.ON_FOOT);
        mRunningContext = new ActivityContext("Running", DetectedActivity.RUNNING);
        mStillContext = new ActivityContext("Still", DetectedActivity.STILL);
        mTiltingContext = new ActivityContext("Tilting", DetectedActivity.TILTING);
        mUnknownContext = new ActivityContext("Unknown", DetectedActivity.UNKNOWN);
        mWalkingContext = new ActivityContext("Walking", DetectedActivity.WALKING);
        mMovingContext = new ContextNot("Moving", mStillContext);
        mMoving2Context = new ContextAnd("Moving, on foot", mMovingContext, mOnFootContext);

        mActivitySensor = new ActivitySensor(this); // , 2000);
        mActivitySensor.registerValueListener(mInVehicleContext);
        mActivitySensor.registerValueListener(mOnBicycleContext);
        mActivitySensor.registerValueListener(mOnFootContext);
        mActivitySensor.registerValueListener(mRunningContext);
        mActivitySensor.registerValueListener(mStillContext);
        mActivitySensor.registerValueListener(mTiltingContext);
        mActivitySensor.registerValueListener(mUnknownContext);
        mActivitySensor.registerValueListener(mWalkingContext);

        mMyContexts = new ArrayList<Context>();
        mMyContexts.add(locationContext1);
        mMyContexts.add(locationContext2);

        mMyContexts.add(mInVehicleContext);
        mMyContexts.add(mOnBicycleContext);
        mMyContexts.add(mOnFootContext);
        mMyContexts.add(mRunningContext);
        mMyContexts.add(mStillContext);
        mMyContexts.add(mTiltingContext);
        mMyContexts.add(mUnknownContext);
        mMyContexts.add(mWalkingContext);
        mMyContexts.add(mMovingContext);
        mMyContexts.add(mMoving2Context);

        if(!checkPermission(permissions[0]) || !checkPermission(permissions[1])) {
            requestPermissions(permissions);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mycontexts_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.my_contexts:
                Log.i(TAG, "MyContexts");
                startActivity(new Intent(this, MyContextsActivity.class));
                return true;
            case R.id.my_contexts_dialog:
                Log.i(TAG, "MyContexts dialog");
                MyContextsDialog dialog = new MyContextsDialog(this, mMyContexts);
                // dialog.setContentView(R.layout.mycontexts_dialog);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles the Start Context Updates button
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates && checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            mLocationSensor.startUpdates();
        }
        if (!mRequestingActivityUpdates && checkPermission(activityPermission)) {
            mRequestingActivityUpdates = true;
            mActivitySensor.startUpdates();
        }
    }

    /**
     * Handles the Stop Context Updates button, stop location updates
     */
    public void stopUpdatesButtonHandler(View view) {
        mLocationSensor.stopUpdates();
        mRequestingLocationUpdates = false;

        mActivitySensor.stopUpdates();
        mRequestingActivityUpdates = false;

        setButtonsEnabledState();
    }

    /**
     * Disables/enables start and stop buttons
     */
    private void setButtonsEnabledState() {
        mStartUpdatesButton.setEnabled(!mRequestingLocationUpdates);
        mStopUpdatesButton.setEnabled(mRequestingLocationUpdates);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove location updates
        if (mRequestingLocationUpdates) {
            mLocationSensor.stopUpdates();
        }
        // Stop updates from activity sensor
        if (mRequestingActivityUpdates) {
            mActivitySensor.stopUpdates();
        }
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermission(String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(this, permission);
        Log.i(TAG, "checkPermission: " + permission + " " + (permissionState == PackageManager.PERMISSION_GRANTED));
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request permissions
     */
    private void requestPermissions(String[] permissions) {
        boolean shouldProvideRationale = false;
        for (String permission : permissions) {
            shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            if (shouldProvideRationale) {
                showSnackbar(R.string.permission_rationale,
                        android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request permission
                                ActivityCompat.requestPermissions(MyContextsExample.this,
                                        permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                            }
                        });
                return;
            }
        }
        ActivityCompat.requestPermissions(MyContextsExample.this,
                    permissions, REQUEST_PERMISSIONS_REQUEST_CODE);

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
                return;
            }
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission" + permissions[i] + " granted");
                } else { // Permission denied.
                    Log.i(TAG, "Permission" + permissions[i] + " denied");
                }
            }
        }
    }

}
