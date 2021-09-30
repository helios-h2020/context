package eu.h2020.helios_social.core.info_control;

import android.app.Activity;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.location.DetectedActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.context.ContextAnd;
import eu.h2020.helios_social.core.context.ContextOr;
import eu.h2020.helios_social.core.context.ext.ActivityContext;
import eu.h2020.helios_social.core.sensor.Sensor;
import eu.h2020.helios_social.core.sensor.ext.ActivitySensor;

/**
 * MyContexts instrumented test, which will execute on an Android device.
 *
 * @see MyContexts
 */
@RunWith(AndroidJUnit4.class)
public class MyContextsInstrumentedTest {
    @Test
    public void myContextsDbTest() {

        android.content.Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Init sensors
        ActivitySensor activitySensor1 = new ActivitySensor("activity_sensor1", appContext, 0);

        // Init MyContexts, and add contexts
        MyContexts myContexts = new MyContexts(null, MyContextsDatabase.getDatabase(appContext));

        List<Context> contexts = myContexts.getContexts();
        for(Context context : contexts) {
            if (context != null) {
                System.out.println("Context=" + context.getName() + "," + context.getId());
            }
        }
        if(contexts.size() > 0) {
            System.out.println("contexts size1="+contexts.size());
            ActivityContext activity1 = (ActivityContext)myContexts.getContextById("activity_context1");
            if(activity1 != null) {
                System.out.println("activity context1:" + activity1.getName() );
                Iterator<Sensor> sensors = activity1.getSensors();
                while(sensors.hasNext()) {
                    Sensor sensor = sensors.next();
                    System.out.println("Context Sensor:" + sensor.getId());
                }
            }
        }
        myContexts.removeAll();

        Context context1 = new Context("id1", "context1", false);
        myContexts.add(context1);
        Context context2 = new Context("context2", false);
        myContexts.add(context2);
        Context context3 = new Context("context3", false);
        myContexts.add(context3);
        context3.setName("context3_updated");

        Context context4 = new ContextOr("context4", context3, context1);
        myContexts.add(context4);
        Context context5 = new ContextAnd("context5", context4, context1);
        myContexts.add(context5);
        Context context6 = new ActivityContext("activity_context1", "activity_context", DetectedActivity.WALKING);
        context6.addSensor(activitySensor1);
        myContexts.add(context6);

        myContexts.remove(context2);
        myContexts.update(context3);

        contexts = myContexts.getContexts();
        System.out.println("contexts size=" + contexts.size());
        for(Context context : contexts) {
            if (context != null) {
                System.out.println("Context name1=" + context.getName() + "," + context.getId());
            }
        }
    }
}
