# Creating new context classes by extending the base Context class

The following example shows how to create a new time-based context class.  The context is defined by given start and end times of the time span when the context is active. 
See the source code in the context repository: lib/src/main/java/eu/h2020/helios_social/core/context/ext/TimeContext.java

First, the new class `TimeContext` extends the base class `Context` (eu.h2020.helios_social.core.context.Context) as follows:

	public class TimeContext extends Context implements SensorValueListener {

In order to receive updates from a sensor (in this case time updates), the new class needs to implement the `SensorValueListener` interface. 

Then, define some needed local parameters of the class and the class constructor:

        long startTime;
        long endTime;

        /**
        * Creates a TimeContext
        * @param name the name of the context
        * @param startTime the start moment of the time interval (milliseconds since epoch)
        * @param endTime the end moment of the time interval (milliseconds since epoch)
        */
        public TimeContext(String name, long startTime, long endTime) {
            super(TIME_CONTEXT_TYPE, name, false);
            this.startTime = startTime;
            this.endTime = endTime;
        }
    
After, you need to implement the SensorValueListener interface's method `receiveValue`:

        /**
        * Receive updated time values from the time sensor.
        * For example, from the TimeSensor {@see eu.h2020.helios_social.core.sensor.ext.TimeSensor}.
        * In order to receive the time updates, this context should be registered as a SensorValueListener
        * for the sensor.
        * @param value the received value
        */
        @Override
        public void receiveValue(Object value) {
            long currentTime = (Long) value;
            // if the current time is between the startTime and endTime
            // set the context active (True), otherwise inactive (False)
            setActive(startTime <= currentTime && endTime >= currentTime);
        } 
    } /* end of TimeContext definition */

As in the above, you should use always the method `setActive(boolean)` (inherited from the base Context class) to update
the active value of the context. In principle, you can use any ways to determine, compute and automatically update the active 
value based on the information available for the context (context attribute values, data sources, sensors). In this case, 
it was only required to check if the current time is within the defined time internal of the context.

## Sensors and data sources ##

The TimeContext above implements the `SensorValueListener` interface. In the following we are showing how to create a 
new Sensor class for obtaining the time information required by the TimeContext to automatically update the context active value. 
Further, the context (implementing the SensorValueListener interface) should be registered for the sensor to receive the updated data 
values from the sensor.
The following outlines the implementation of a new simple `TimeSensor` class. The time is obtained directly as the system time, 
and sensor value listeners are notified with given time interval about the current time.
See the source code in the context repository: lib/src/main/java/eu/h2020/helios_social/core/sensor/ext/TimeSensor.java

First, the new class extends the abstract base class Sensor:

    public class TimeSensor extends Sensor {
        int timeInterval;
        ScheduledExecutorService scheduler;
        /**
        * Creates a new TimeSensor
        * @param timeInterval the time interval in milliseconds
        */
        public TimeSensor(int timeInterval) {
            this.timeInterval = timeInterval;
            this.scheduler = null;
        }
        
Then, the subclasses of Sensor need to implement the methods `startUpdates` and `stopUpdates` of the base Sensor class.  
The startUpdates is used to start receiving data values from the server. In typical implementation, when a new value is 
available from the sensor the registered SensorValueListeners are notified, and the new value is passed via the listener to the context. 
In the following, a scheduler thread is started and every period of given time interval it will call the `receiveValue` method of the base 
class Sensor that cause the registered listeners notified the new value:

        @Override
        public void startUpdates() {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleWithFixedDelay(
                new Runnable() {
                    public void run() {
                        receiveValue(System.currentTimeMillis());
                    }
                }, 0, timeInterval, TimeUnit.MILLISECONDS);
        }
    
finally, provide the implementation for the stopUpdates method. The following code stops the scheduler thread:

        @Override
        public void stopUpdates() {
            if(scheduler != null) {
                // stops the scheduler thread
                scheduler.shutdownNow();
            }
        } 
    } /* end of TimeSensor class definition */


## A usage example ##

For example,  an application can create instances of the new TimeContext class as follows:

    TimeContext timecontext1 = new TimeContext("timecontext1", startTime, endTime);
    
Then, create TimeSensor for obtaining current time value updates for the context in 1 second intervals:

    TimeSensor timeSensor = new TimeSensor(1000);  // update interval = 1000 milliseconds
    
After, the new timeSensor should be registered for the context in order to obtain automatically time value updates:

    timeSensor.registerValueListener(timecontext1);
    
Then, start receiving the updates:

    timeSensor.startUpdates();

After, the application can in any time check whether the context is active by using the `isActive` method of the base class Context:

    Boolean activeValue = timeSensor.isActive();

If the application would like to automatically obtain notifications about the updated active value then the application should implement the 
`ContextListener` interface (eu.h2020.helios_social.core.context.ContextListener), which includes the method 
`contextChanged` that should implement by the application:

    @Override
    public void contextChanged(boolean active) {        
        // do something with the received active value of the context. For example, update the UI:
        updateUI();
    }

Then, register this listener to obtain changes in the context active value:

    timeContext1.registerContextListener(this);

After, whenever the context active value is changed, the contextChanged method is also called and the application 
obtains the new context active value.
