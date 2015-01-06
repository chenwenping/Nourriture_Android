package team_10.nourriture_android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.activity.NotificationActivity;
import team_10.nourriture_android.activity.NourritureRestClient;
import team_10.nourriture_android.bean.NotificationBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

public class PollingService extends Service {

    public static final String ACTION = "team_10.nourriture_android.service.PollingService";
    private static final String NOTIFICATION_DATA_PATH = "_notification_data.bean";
    int count = 0;
    private Notification mNotification;
    private NotificationManager mManager;
    private List<NotificationBean> unReadNotificationList;
    private int notification_num = 0;
    private SharedPreferences sp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initNotificationManager();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        new PollingThread().start();
    }

    private void initNotificationManager() {
        mManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        mNotification = new Notification();
        mNotification.icon = icon;
        mNotification.tickerText = "New Message";
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    private void showNotification() {
        mNotification.when = System.currentTimeMillis();
        //Navigator to the new activity when click the notification title
        Intent intent = new Intent(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotification.setLatestEventInfo(this,
                getResources().getString(R.string.app_name), "You have new message.", pendingIntent);
        mManager.notify(0, mNotification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }

    private void getMyUnreadNotifications() {
        sp = getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        String username = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
        NourritureRestClient.getWithLogin("getMyUnreadNotifications", null, username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("getMyUnreadNotifications", response.toString());
                if (statusCode == 200) {
                    try {
                        unReadNotificationList = JsonTobean.getList(NotificationBean[].class, response.toString());
                        Collections.reverse(unReadNotificationList);
                        ObjectPersistence.writeObjectToFile(getApplicationContext(), unReadNotificationList, NOTIFICATION_DATA_PATH);
                        if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                            notification_num = unReadNotificationList.size();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getLocalNotificationData();
                    if (unReadNotificationList != null && unReadNotificationList.size() > 0) {
                        notification_num = unReadNotificationList.size();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getLocalNotificationData() {
        List<NotificationBean> localNotificationList = (List<NotificationBean>) ObjectPersistence.readObjectFromFile(getApplicationContext(), NOTIFICATION_DATA_PATH);
        if (localNotificationList != null) {
            unReadNotificationList = localNotificationList;
        }
    }

    class PollingThread extends Thread {
        @Override
        public void run() {
            System.out.println("Polling...");
            count++;
            if (count % 2 == 0) {
                showNotification();
                System.out.println("New message!");
            }
        }
    }
}
