package eu.h2020.helios_social.core.info_control;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.context.ContextAnd;
import eu.h2020.helios_social.core.context.ContextNot;
import eu.h2020.helios_social.core.context.ContextOr;
import eu.h2020.helios_social.core.context.ext.ActivityContext;
import eu.h2020.helios_social.core.context.ext.LocationContext;
import eu.h2020.helios_social.core.context.ext.TimeContext;
import eu.h2020.helios_social.core.context.ext.WifiContext;
import eu.h2020.helios_social.core.sensor.Sensor;

/**
 * MyContextsEntity class represents a context entity of MyContexts, and it sis associated with MyContextsDatabase.
 */
@Entity(tableName = "mycontexts_table")
public class MyContextsEntity {
    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "context_id")
    private final String contextId;   // user active context when read the message

    @ColumnInfo(name = "name")
    private final String name;
    @ColumnInfo(name = "active")
    private final boolean active; //
    @ColumnInfo(name = "class")
    private final String contextClass;
    @ColumnInfo(name = "sensors")
    private final String sensorIds;
    @ColumnInfo(name = "parameters")
    private final String parameters;

    public MyContextsEntity(String contextId, String name, boolean active, String contextClass, String sensorIds, String parameters) {
        this.contextId = contextId;
        this.name = name;
        this.active = active;
        this.contextClass = contextClass;
        this.sensorIds = sensorIds;
        this.parameters = parameters;
    }

    /**
     * Creates a MyContextsEntity
     *
     * @param context the Context
     */
    public MyContextsEntity(@NonNull Context context) throws JSONException {
        this.contextId = context.getId();
        this.name = context.getName();
        this.active = context.isActive();
        this.contextClass = context.getClass().getCanonicalName();
        String sensorIds = "";
        Iterator<Sensor> sensors = context.getSensors();
        int i=0;
        while (sensors.hasNext()) {
            Sensor sensor = sensors.next();
            if(i>0) {
                sensorIds += ',';
            }
            sensorIds += sensor.getId();
            i++;
        }
        this.sensorIds = sensorIds;
        this.parameters = MyContextsEntity.getParametersJSON(context).toString();
    }

    @NonNull
    public String getContextId() {
        return contextId;
    }

    public String getName() {
        return name;
    }

    public boolean getActive() {
        return active;
    }

    public String getSensorIds() {
        return sensorIds;
    }

    public String getContextClass() {
        return contextClass;
    }

    public String getParameters() {
        return parameters;
    }

    private List<Sensor> getSensors() {
        String[] sensorIds = this.sensorIds.split(",");
        List<Sensor> sensorList = new ArrayList<Sensor>();
        for (String id : sensorIds) {
            Sensor sensor = Sensor.getSensorById(id);
            if (sensor != null && !sensorList.contains(sensor)) {
                sensorList.add(sensor);
            }
        }
        return sensorList;
    }

    Context getContext(@NonNull Map<String, Context> myContexts) throws JSONException, ClassNotFoundException {
        Class contextClass = Class.forName(getContextClass());
        String contextId = getContextId();
        String name = getName();
        boolean active = getActive();
        List<Sensor> sensors = getSensors();
        JSONObject parameters = new JSONObject(getParameters());
        Context context = null;
        if (ContextOr.class.isAssignableFrom(contextClass)) {
            Context contextA = myContexts.get(parameters.getString("contextA"));
            Context contextB = myContexts.get(parameters.getString("contextB"));
            if(contextA != null && contextB != null) {
                context = new ContextOr(contextId, name, contextA, contextB);
            }
        } else if (ContextAnd.class.isAssignableFrom(contextClass)) {
            Context contextA = myContexts.get(parameters.getString("contextA"));
            Context contextB = myContexts.get(parameters.getString("contextB"));
            if(contextA != null && contextB != null) {
                context = new ContextAnd(contextId, name, contextA, contextB);
            }
        } else if (ContextNot.class.isAssignableFrom(contextClass)) {
            Context contextNot = myContexts.get(parameters.getString("context"));
            if(contextNot != null) {
                context = new ContextNot(contextId, name, contextNot);
            }
        } else if (ActivityContext.class.isAssignableFrom(contextClass)) {
            int activityType = parameters.getInt("activity_type");
            context = new ActivityContext(contextId, name, activityType);
        } else if (LocationContext.class.isAssignableFrom(contextClass)) {
            double latitude = parameters.getDouble("latitude");
            double longitude = parameters.getDouble("longitude");
            double radius = parameters.getDouble("radius");
            context = new LocationContext(contextId, name, latitude, longitude, radius);
        } else if (TimeContext.class.isAssignableFrom(contextClass)) {
            long startTime = parameters.getLong("start_time");
            long endTime = parameters.getLong("end_time");
            context = new TimeContext(contextId, name, startTime, endTime);
        } else if (WifiContext.class.isAssignableFrom(contextClass)) {
            String ssid = parameters.getString("ssid");
            context = new WifiContext(contextId, name, ssid);
        } else if (Context.class.isAssignableFrom(contextClass)) {
            context = new Context(contextId, name, active);
        }
        if (context != null) {
            context.addSensors(sensors);
        }
        return context;
    }

    /**
     * Returns this Context class-specific parameters as a JSONObject.
     * This method is used in storing the context into databases.
     *
     * @return the JSONObject
     */
    private static JSONObject getParametersJSON(Context context) throws JSONException {
        JSONObject json =  new JSONObject();
        if(context instanceof ContextOr) {
            json.put("contextA", ((ContextOr)context).getContextA().getId());
            json.put("contextB", ((ContextOr)context).getContextB().getId());
        } else if(context instanceof ContextAnd) {
            json.put("contextA", ((ContextAnd)context).getContextA().getId());
            json.put("contextB", ((ContextAnd)context).getContextB().getId());
        } else if(context instanceof ContextNot) {
            json.put("context", ((ContextNot)context).getContextNot().getId());
        } else if(context instanceof ActivityContext) {
            json.put("activity_type", ((ActivityContext)context).getActivityType());
            json.put("confidence", ((ActivityContext)context).getConfidence());
        } else if(context instanceof LocationContext) {
            json.put("latitude", ((LocationContext)context).getLat());
            json.put("longitude", ((LocationContext)context).getLon());
            json.put("radius", ((LocationContext)context).getRadius());
        } else if(context instanceof WifiContext) {
            json.put("ssid", ((WifiContext)context).getSsid());
        } else if(context instanceof TimeContext) {
            json.put("start_time", ((TimeContext)context).getStartTime());
            json.put("end_time", ((TimeContext)context).getEndTime());
        }
        return json;
    }

}
