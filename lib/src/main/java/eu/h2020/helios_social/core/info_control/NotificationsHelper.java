package eu.h2020.helios_social.core.info_control;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Iterator;

import eu.h2020.helios_social.core.profile.HeliosProfileManager;

public class NotificationsHelper {
    private static final String TAG = "NotificationsHelper";
    private static final NotificationsHelper ourInstance = new NotificationsHelper();
    private Context mContext;
    private Class<?> mCallerClass;
    private Class<?> mMainClass;
    private InfoControl mInfoControl;
    private int testNotificationId = 0;
    private final HeliosProfileManager mProfileManager = HeliosProfileManager.getInstance();

    private static ArrayList<ArrayList<Pair<String, String>>> mNotifList = new ArrayList<>();
    private static boolean[] mNotifTimerOn = new boolean[5];

    public static final String CHANNEL_VERY_LOW_ID = "HELIOS_messaging_very_low";
    public static final String CHANNEL_LOW_ID = "HELIOS_messaging_low";
    public static final String CHANNEL_MEDIUM_ID = "HELIOS_messaging_medium";
    public static final String CHANNEL_HIGH_ID = "HELIOS_messaging_high";
    public static final String CHANNEL_VERY_HIGH_ID = "HELIOS_messaging_very_high";

    public static final String GROUP_HELIOS = "eu.h2020.helios_social.HELIOS_MESSAGE_GROUP";

    private NotificationsHelper() {
        Log.d(TAG, "NotificationsHelper()");
    }

    public static NotificationsHelper getInstance() {
        return ourInstance;
    }

    public void setCallerParams(Context ctx, Class<?> callerClass, Class<?> mainClass, InfoControl infoControl) {
        Log.d(TAG, "setCallerParams");
        mContext = ctx;
        mCallerClass = callerClass;
        mMainClass = mainClass;
        mInfoControl = infoControl;
    }

    public void createChannels() {
        // Create the NotificationChannel, but only on API 26+ (8/Oreo +) because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Very low importance channel
            NotificationChannel channelVeryLow = new NotificationChannel(CHANNEL_VERY_LOW_ID, "HELIOS_Message_Channel", NotificationManager.IMPORTANCE_MIN);
            channelVeryLow.setDescription("HELIOS Channel description");

            // Low importance channel
            NotificationChannel channelLow = new NotificationChannel(CHANNEL_LOW_ID, "HELIOS_Message_Channel", NotificationManager.IMPORTANCE_LOW);
            channelLow.setDescription("HELIOS Channel description");

            // Medium importance channel
            NotificationChannel channelMedium = new NotificationChannel(CHANNEL_MEDIUM_ID, "HELIOS_Message_Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channelMedium.setDescription("HELIOS Channel description");

            // High importance channel
            NotificationChannel channelHigh = new NotificationChannel(CHANNEL_HIGH_ID, "HELIOS_Message_Channel", NotificationManager.IMPORTANCE_HIGH);
            channelHigh.setDescription("HELIOS Channel description");

            // Very high importance channel
            // NotificationChannel channelVeryHigh = new NotificationChannel(CHANNEL_VERY_HIGH_ID, "HELIOS_Message_Channel", NotificationManager.IMPORTANCE_MAX);
            NotificationChannel channelVeryHigh = new NotificationChannel(CHANNEL_VERY_HIGH_ID, "HELIOS_Message_Channel", NotificationManager.IMPORTANCE_HIGH);
            channelVeryHigh.setDescription("HELIOS Channel description");

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channelVeryLow);
            notificationManager.createNotificationChannel(channelLow);
            notificationManager.createNotificationChannel(channelMedium);
            notificationManager.createNotificationChannel(channelHigh);
            notificationManager.createNotificationChannel(channelVeryHigh);
        }
        // Create arraylists for delayed notifications
        int i;
        for (i = 0; i < 5; i++) {
            ArrayList<Pair<String, String>> al = new ArrayList<>();
            mNotifList.add(al);
        }
        // Get notification preferences and init delayed handlers
        initNotification("vhi", NotificationCompat.PRIORITY_MAX);
        initNotification("hi", NotificationCompat.PRIORITY_HIGH);
        initNotification("mi", NotificationCompat.PRIORITY_DEFAULT);
        initNotification("li", NotificationCompat.PRIORITY_LOW);
        initNotification("vli", NotificationCompat.PRIORITY_MIN);
    }

    /**
     * Get received message importance (priority) in context
     * @param title
     * @param message
     * @return the priority value
     */
    private int getMessagePriority(String title, String message) {
        if(mInfoControl == null || mInfoControl.getActiveContexts().size() == 0) {
            return NotificationCompat.PRIORITY_DEFAULT;
        }
        int importance = MessageImportance.IMPORTANCE_VERY_LOW;
        for (eu.h2020.helios_social.core.context.Context context : mInfoControl.getActiveContexts()) {
            int messageImportance = mInfoControl.getMessageImportance(new MessageInfo(null, 0, title, message), context);
            if (messageImportance > importance) {
                importance = messageImportance;
            }
        }
        switch (importance) {
            case MessageImportance.IMPORTANCE_VERY_HIGH:
                return NotificationCompat.PRIORITY_MAX;
            case MessageImportance.IMPORTANCE_HIGH:
                return NotificationCompat.PRIORITY_HIGH;
            case MessageImportance.IMPORTANCE_MEDIUM:
                return NotificationCompat.PRIORITY_DEFAULT;
            case MessageImportance.IMPORTANCE_LOW:
                return NotificationCompat.PRIORITY_LOW;
            case MessageImportance.IMPORTANCE_VERY_LOW:
                return NotificationCompat.PRIORITY_MIN;
        }
        return NotificationCompat.PRIORITY_DEFAULT;
    }

    // Show notification of a message received.
    public void showNotification(String title, String message) {
        Log.d(TAG, "showNotification title:" + title + " message:" + message);

        // Context ctx = this.getApplicationContext();
        Intent notificationIntent = new Intent(mContext, mMainClass);
        // Open the app to the state it was in if opened.
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        // gets the message's contextual importance (priority)
        int priority = getMessagePriority(title, message);
        String channelId = getChannelId(priority);
        String prefix = "mi";
        
        switch(priority) {
        case NotificationCompat.PRIORITY_MAX:
            prefix = "vhi";
            break;
        case NotificationCompat.PRIORITY_HIGH:
            prefix = "hi";
            break;
        case NotificationCompat.PRIORITY_DEFAULT:
            prefix = "mi";
            break;
        case NotificationCompat.PRIORITY_LOW:
            prefix = "li";
            break;
        case NotificationCompat.PRIORITY_MIN:
            prefix = "vli";
            break;
        }

        int icon = getIcon(prefix);
        int color = getColor(prefix);
        int interval = getInterval(prefix);

        if (interval > 0) {
            // Delayed notifications. Save message to list
            Pair<String, String> msg = new Pair<>(title, message);
            mNotifList.get(priority + 2).add(msg);
            if (!mNotifTimerOn[priority + 2])
                // Preferences are changed
                initNotification(prefix, priority);
        } else {
            // On Android 7 N (API 24) and higher the system groups notifications. Let's not make
            // a separate group for normal notifications now.
            // Build the notification and add the action.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setSmallIcon(icon)
                .setColor(color)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(priority)
                .setAutoCancel(true);
            // Issue the notification.
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
            notificationManager.notify(++testNotificationId, builder.build());
        }
    }

    public Notification getNotification() {
        // Context ctx = this.getApplicationContext();
        Intent notificationIntent = new Intent(mContext, mMainClass);
        // Open the app to the state it was in if opened.
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        // Group this notification to GROUP_HELIOS -- in order to show Service notification
        // separately from other notifications.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_MEDIUM_ID)
                .setColor(Color.GREEN)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle("HELIOS")
                .setContentText("Application running.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(GROUP_HELIOS)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        return builder.build();
    }

    public Notification getServiceNotification(String stopAction) {
        // Context ctx = this.getApplicationContext();
        Intent notificationIntent = new Intent(mContext, mMainClass);
        // Open the app to the state it was in if opened.
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        Intent stopIntent = new Intent(mContext, mCallerClass);
        stopIntent.setAction(stopAction);
        PendingIntent pendingIntentStop = PendingIntent.getService(mContext, 0,
                stopIntent, 0);

        // Group this notification to GROUP_HELIOS -- in order to show Service notification
        // separately from other notifications.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_MEDIUM_ID)
                .setColor(Color.GREEN)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle("HELIOS is connected and running in the background.")
                .setContentText("Tap below to disconnect.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(GROUP_HELIOS)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_menu_delete, "Disconnect/Stop Service",
                        pendingIntentStop);

        return builder.build();
    }

    public void setAppNotification(int ONGOING_NOTIFICATION_ID) {
        NotificationManagerCompat mgr =
                NotificationManagerCompat.from(mContext);
        mgr.notify(ONGOING_NOTIFICATION_ID, getNotification());
    }

    public void setServiceNotification(int ONGOING_NOTIFICATION_ID, String stopAction) {
        NotificationManagerCompat mgr =
                NotificationManagerCompat.from(mContext);
        mgr.notify(ONGOING_NOTIFICATION_ID, getServiceNotification(stopAction));
    }

    private void initNotification(String prefix, int priority) {
        int interval = getInterval(prefix);
        if (interval > 0) {
            // Start periodic timer for checking messages
            mNotifTimerOn[priority + 2] = true;
            String channelId = getChannelId(priority);
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable notifCheck = new Runnable() {
                    @Override
                    public void run()  {
                        ArrayList<Pair<String, String>> msgs = mNotifList.get(priority + 2);
                        if (!msgs.isEmpty()) {
                            Intent notificationIntent = new Intent(mContext, mMainClass);
                            // Open the app to the state it was in if opened.
                            notificationIntent.setAction(Intent.ACTION_MAIN);
                            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent =
                                PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
                            int icon = getIcon(prefix);
                            int color = getColor(prefix);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                                .setSmallIcon(icon)
                                .setColor(color)
                                .setContentIntent(pendingIntent)
                                .setPriority(priority)
                                .setAutoCancel(true);
                            int numMsg = msgs.size();
                            if (numMsg == 1) {
                                Pair<String, String> msg = msgs.get(0);
                                builder.setContentTitle(msg.first);
                                builder.setContentText(msg.second);
                                msgs.remove(0);
                            } else {
                                NotificationCompat.InboxStyle msgList = new NotificationCompat.InboxStyle();
                                String title = "" + numMsg + " new messages";
                                msgList.setBigContentTitle(title);
                                Iterator<Pair<String, String>> itr = msgs.iterator();
                                while (itr.hasNext())
                                {
                                    Pair<String, String> msg = itr.next();
                                    msgList.addLine(msg.second);
                                    itr.remove();
                                }
                                builder.setContentTitle(title);
                                builder.setStyle(msgList);
                            }
                            // Issue the notification.
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                            notificationManager.notify(++testNotificationId, builder.build());
                        }
                        // Check if preferences have been changed
                        int current_interval = getInterval(prefix);
                        if (current_interval != 0) {
                            // Schedule next check
                            handler.postDelayed(this, current_interval);
                        } else  // Stop periodic check
                            mNotifTimerOn[priority + 2] = false;
                    }
                };
            handler.postDelayed(notifCheck, interval);
        }
    }

    private int getIcon(String prefix) {
        String iconStr = mProfileManager.load(mContext, prefix + "_icon");
        int iconVal;
        try {
            iconVal = Integer.parseInt(iconStr);
        } catch (NumberFormatException e) {
            iconVal = 0;
        }
        int icon;
        switch(iconVal) {
        case 0:
            icon = android.R.drawable.star_on;
            break;
        case 1:
            icon = android.R.drawable.star_big_on;
            break;
        default:
            icon = android.R.drawable.star_on;
        }
        return icon;
    }

    private int getColor(String prefix) {
        String colorStr = mProfileManager.load(mContext, prefix + "_color");
        int colorVal;
        try {
            colorVal = Integer.parseInt(colorStr);
        } catch (NumberFormatException e) {
            colorVal = 0;
        }
        int color;
        switch(colorVal) {
        case 0:
           color = Color.BLACK;
            break;
        case 1:
            color = Color.RED;
            break;
        case 2:
            color = Color.GREEN;
            break;
        case 3:
            color = Color.BLUE;
            break;
        case 4:
            color = Color.YELLOW;
            break;
        case 5:
            color = Color.CYAN;
            break;
        case 6:
            color = Color.MAGENTA;
            break;
        case 7:
            color = Color.DKGRAY;
            break;
        case 8:
            color = Color.GRAY;
            break;
        case 9:
            color = Color.LTGRAY;
            break;
        case 10:
            color = Color.WHITE;
            break;
        default:
            color = Color.BLACK;
        }
        return color;
    }

    private int getInterval(String prefix) {
        String intervalStr = mProfileManager.load(mContext, prefix + "_interval");
        int intervalVal;
        try {
            intervalVal = Integer.parseInt(intervalStr);
        } catch (NumberFormatException e) {
            intervalVal = 0;
        }
        int interval;
        switch(intervalVal) {
        case 0:
           interval = 0;
            break;
        case 1:
            interval = 30000;
            break;
        case 2:
            interval = 60000;
            break;
        case 3:
            interval = 90000;
            break;
        case 4:
            interval = 120000;
            break;
        default:
            interval = 0;
        }
        return interval;
    }

    private String getChannelId(int priority) {
        String channelId = CHANNEL_MEDIUM_ID;
        switch(priority) {
        case NotificationCompat.PRIORITY_MAX:
            channelId = CHANNEL_VERY_HIGH_ID;
            break;
        case NotificationCompat.PRIORITY_HIGH:
            channelId = CHANNEL_HIGH_ID;
            break;
        case NotificationCompat.PRIORITY_DEFAULT:
            channelId = CHANNEL_MEDIUM_ID;
            break;
        case NotificationCompat.PRIORITY_LOW:
            channelId = CHANNEL_LOW_ID;
            break;
        case NotificationCompat.PRIORITY_MIN:
            channelId = CHANNEL_VERY_LOW_ID;
            break;
        }
        return channelId;
    }
}
