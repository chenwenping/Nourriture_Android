package team_10.nourriture_android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import team_10.nourriture_android.R;
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
        PollingUtils.startPollingService(this, 5, PollingService.class, PollingService.ACTION);

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
    protected void onDestroy() {
        super.onDestroy();
        // stop polling service
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);

    }
}
