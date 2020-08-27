package eu.h2020.helios_social.core.sensor.ext;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import eu.h2020.helios_social.core.sensor.Sensor;

/**
 *  This class provides an implementation of sensor API for detecting activities.
 *  ActivitySensor is a subclass of the abstract base class Sensor.
 *  It uses Android ActivityRegnitionClient API to detect activities and to update their values,
 *  as well as start and stop the activity detection updates
 */
public class ActivitySensor extends Sensor {

    private final ActivityRecognitionClient mActivityRecognitionClient;
    private PendingIntent mPendingIntent;
    private Intent mIntent;
    private long mInterval;
    private ActivityReceiver mActivityReceiver;
    private boolean activityTrackingEnabled;

    private ContextWrapper appEnv;
    // Action fired when activity updates are triggered.
    private static final String RECEIVER_ACTION = "HELIOS_ACTIVITY_SENSOR_RECEIVER_ACTION";
    private static final String TAG = "HeliosActivitySensor";

    /**
     * Creates a ActivitySensor
     * @param appEnv the application env
     * @param interval the detection interval
     */
    public ActivitySensor(ContextWrapper appEnv, long interval) {
        this.mActivityRecognitionClient = ActivityRecognition.getClient(appEnv);
        this.appEnv = appEnv;
        this.mInterval = interval;
        activityTrackingEnabled = false;
        createActivityRecognizer();
    }

    /**
     * Creates a ActivitySensor
     * @param appEnv the application env
     */
    public ActivitySensor(ContextWrapper appEnv) {
        this(appEnv, 0);
    }

    @Override
    public void startUpdates() {
        mActivityRecognitionClient.requestActivityUpdates(mInterval, mPendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        activityTrackingEnabled = true;
                        appEnv.registerReceiver(
                                mActivityReceiver,
                                new IntentFilter(RECEIVER_ACTION)
                        );
                        Log.d(TAG, "Successfully requested activity updates");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Requesting activity updates failed to start");
                    }
                });
    }

    @Override
    public void stopUpdates() {
        mActivityRecognitionClient.removeActivityUpdates(mPendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        activityTrackingEnabled = false;
                        appEnv.unregisterReceiver(mActivityReceiver);
                        Log.d(TAG,"Activity updates successfully unregistered.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Activity updates could not be unregistered: " + e);
                    }
                });
        Log.d(TAG, "stopUpdates");
    }

    private void createActivityRecognizer() {
        mIntent = new Intent(RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(appEnv, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // The receiver listens for the PendingIntent above that is triggered by the system when an activity detected
        mActivityReceiver = new ActivityReceiver();
        Log.d(TAG, "ActivityRecognizer created");
    }

    /**
     * Handles intents from from the Activity Recognition API
     */
    public class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"ActivityReceiver; onReceive()");
            if (!intent.getAction().equals(RECEIVER_ACTION)) {
                Log.d(TAG, "Received an unsupported action in ActivityReceiver: action = " +
                        intent.getAction());
                return;
            }
            // receive result, i.e, detected activities with confidence values
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            // Sensor receive value
            receiveValue(result);
        }
    }
}