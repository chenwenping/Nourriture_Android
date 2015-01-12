package team_10.nourriture_android.activity;

import android.app.Notification;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import team_10.nourriture_android.R;
import team_10.nourriture_android.push.Utils;
import team_10.nourriture_android.service.PollingService;
import team_10.nourriture_android.service.PollingUtils;

public class MainActivity extends ActionBarActivity {

    private Button btn_dishes;
    private Button btn_myRecipes;
    private Button btn_notification;
    private Button btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start polling service
        PollingUtils.startPollingService(this, 2, PollingService.class, PollingService.ACTION);

        Resources resource = this.getResources();
        String pkgName = this.getPackageName();

        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(MainActivity.this,
        // "api_key")
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(MainActivity.this, "api_key"));
        // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
        // PushManager.enableLbs(getApplicationContext());

        // Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
        // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
        // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                getApplicationContext(), resource.getIdentifier(
                "notification_custom_builder", "layout", pkgName),
                resource.getIdentifier("notification_icon", "id", pkgName),
                resource.getIdentifier("notification_title", "id", pkgName),
                resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier(
                "ic_launcher", "drawable", pkgName));
        PushManager.setNotificationBuilder(this, 1, cBuilder);

        btn_dishes = (Button) findViewById(R.id.dishes_btn);
        btn_myRecipes = (Button) findViewById(R.id.myRecipes_btn);
        btn_notification = (Button) findViewById(R.id.notification_btn);
        btn_setting = (Button) findViewById(R.id.setting_btn);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentFrame, new DishesFragment()).commit();

        btn_dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentFrame, new DishesFragment()).commit();
            }
        });
        btn_myRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentFrame, new RecipesFragment()).commit();
            }
        });
        btn_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentFrame, new NotificationFragment()).commit();
            }
        });
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentFrame, new SettingFragment()).commit();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stop polling service
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
    }
}
