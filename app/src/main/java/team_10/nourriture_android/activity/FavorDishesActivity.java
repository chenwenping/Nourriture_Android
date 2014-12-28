package team_10.nourriture_android.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.DishAdapter;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;

/**
 * Created by ping on 2014/12/28.
 */
public class FavorDishesActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private List<DishBean> dishesList;
    private SwipeRefreshLayout swipeLayout;
    private ListView dishListView;
    private DishAdapter dishAdapter;
    private boolean isRefresh = false;
    private Button back_btn;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_dishes);

        initView();

        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.show();
        getFavorDishes();
    }

    public void initView(){
        swipeLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        dishListView = (ListView)this.findViewById(R.id.dishListView);
        back_btn = (Button)this.findViewById(R.id.btn_back);
        back_btn.setOnClickListener(this);
    }

    public void getFavorDishes(){

        NourritureRestClient.get("likes", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progress.dismiss();
                if(statusCode == 200){
                    try {
                        dishesList = JsonTobean.getList(DishBean[].class, response.toString());
                        Log.i("ping", response.toString());
                        if(isRefresh){
                            if(dishAdapter.mDishList!=null && dishAdapter.mDishList.size()>0){
                                dishAdapter.mDishList.clear();
                            }
                            dishAdapter.mDishList.addAll(dishesList);
                            isRefresh= false;
                        }else{
                            dishAdapter = new DishAdapter(FavorDishesActivity.this, false);
                            dishAdapter.mDishList.addAll(dishesList);
                        }
                        dishListView.setAdapter(dishAdapter);
                        dishAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    // cache
                    progress.dismiss();
                    Toast.makeText(FavorDishesActivity.this, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                progress.dismiss();
                Toast.makeText(FavorDishesActivity.this, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast.makeText(FavorDishesActivity.this, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast.makeText(FavorDishesActivity.this, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        if(!isRefresh){
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getFavorDishes();
                }
            }, 3000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
}
