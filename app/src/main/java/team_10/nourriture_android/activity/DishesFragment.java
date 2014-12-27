package team_10.nourriture_android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.DishAdapter;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;

public class DishesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private List<DishBean> dishesList;
    private SwipeRefreshLayout swipeLayout;
    private ListView dishListView;
    private DishAdapter dishAdapter;
    private ProgressDialog progress;
    private boolean isRefresh = false;

    private Button addDish_btn;
    private EditText search_text;
    private Button search_btn;

    private boolean isLogin = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dishes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addDish_btn = (Button)getActivity().findViewById(R.id.btn_add_dish);
        search_btn = (Button)getActivity().findViewById(R.id.btn_search);
        search_text = (EditText)getActivity().findViewById(R.id.et_search_text);

        addDish_btn.setOnClickListener(this);
        search_btn.setOnClickListener(this);

        swipeLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        dishListView = (ListView)getActivity().findViewById(R.id.dishListView);

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading...");
        progress.show();
        getAllDishes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_dish:
                if(isLogin){
                    Intent intent = new Intent(getActivity(), DishAddActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_search:
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        if(!isRefresh){
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getAllDishes();
                }
            }, 3000);
        }
    }

    public void getAllDishes() {
        NourritureRestClient.get("dishes", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progress.dismiss();
                try {
                    dishesList = JsonTobean.getList(DishBean[].class, response.toString());
                    Log.i("ping",response.toString());
                    if(isRefresh){
//                        dishAdapter = new DishAdapter(getActivity(), dishesList);
                        dishAdapter.mDishList.clear();
                        dishAdapter.mDishList.addAll(dishesList);
                        isRefresh= false;
                    }else{
                        dishAdapter = new DishAdapter(getActivity(), false);
                        dishAdapter.mDishList.addAll(dishesList);
                    }
                    dishListView.setAdapter(dishAdapter);
                    dishAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}