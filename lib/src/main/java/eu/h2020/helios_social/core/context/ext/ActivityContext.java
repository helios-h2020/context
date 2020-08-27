package eu.h2020.helios_social.core.context.ext;

import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.sensor.SensorValueListener;

/**
 * ActivityContext - an activity-based context class<br/>
 * This class is used to detect an activity of given type. The allowed activity types
 * include: IN_VEHICLE, ON_FOOT, RUNNING, WALKING, ON_BICYCLE and STILL.
 * @see com.google.android.gms.location.DetectedActivity
 * This class extends the base class Context.
 * Activity value updates are obtained from ActivitySensor via SensorValueListener
 */
public class ActivityContext extends Context implements SensorValueListener {

    private int activityType;
    private int confidence;
    private static final int ACTIVITY_CONTEXT_TYPE=3;
    private static final String TAG = "HeliosActivityContext";

    /**
     * Creates a ActivityContext
     * @param name the name of the context
     * @param activityType the activity type. @see com.google.android.gms.location.DetectedActivity
     */
    public ActivityContext(String name, int activityType) {
        super(ACTIVITY_CONTEXT_TYPE, name, false);
        this.activityType = activityType;
        this.confidence = 0;
    }

    /**
     * Gets the confidence of the activity
     * @return the confidence value
     */
    public int getConfidence() {
        return confidence;
    }

    /**
     * Receive updated activity values from the activity sensor
     * {@link eu.h2020.helios_social.core.sensor.ext.ActivitySensor}.
     * In order to receive the activity updates, this context should be registered as a SensorValueListener
     * for the sensor.
     * @param value the received value
     */
    @Override
    public void receiveValue(Object value) {
        Log.d(TAG, "received activity value");
        ActivityRecognitionResult result = (ActivityRecognitionResult) value;
        confidence = result.getActivityConfidence(activityType);
        Log.d(TAG, "received confidence value: " + confidence);
        setActive((confidence >= 70 || (isActive() && confidence >= 30) ));
    }
}
