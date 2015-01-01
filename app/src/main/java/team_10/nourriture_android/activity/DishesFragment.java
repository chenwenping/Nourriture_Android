package team_10.nourriture_android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.DishAdapter;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.ObjectPersistence;
import team_10.nourriture_android.utils.SharedPreferencesUtil;

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

    private Context mContext;
    private boolean isLogin;
    private int request = 1;

    private static final String DISHES_DATA_PATH="_dishes_data.bean";
    private SharedPreferences sp;

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
        mContext = getActivity();

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

        sp = getActivity().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading...");
        progress.show();

        getAllDishes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_dish:
                //isLogin = MyApplication.getInstance().isLogin();
                isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);
                Log.e("isLogin", String.valueOf(isLogin));
                if(isLogin){
                    Intent intent = new Intent(getActivity(), DishAddActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, request);
                }
                break;
            case R.id.btn_search:
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==LoginActivity.KEY_IS_LOGIN){
            isLogin = true;
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
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(progress.isShowing()){
                    progress.dismiss();
                }
                if(statusCode == 200){
                    try {
                        dishesList = JsonTobean.getList(DishBean[].class, response.toString());
                        Log.i("ping", response.toString());
                        ObjectPersistence.writeObjectToFile(mContext, dishesList, DISHES_DATA_PATH);
                        if(isRefresh){
                            //dishAdapter = new DishAdapter(getActivity(), dishesList);
                            if(dishAdapter.mDishList!=null && dishAdapter.mDishList.size()>0){
                                dishAdapter.mDishList.clear();
                            }
                            dishAdapter.mDishList.addAll(dishesList);
                            isRefresh= false;
                        }else{
                            dishAdapter = new DishAdapter(mContext, false);
                            dishAdapter.mDishList.addAll(dishesList);
                        }
                        dishListView.setAdapter(dishAdapter);
                        dishAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                    getLocalDishesData();
                    if (dishesList != null && dishesList.size()>0) {
                        if(isRefresh){
                            if(dishAdapter.mDishList!=null && dishAdapter.mDishList.size()>0){
                                dishAdapter.mDishList.clear();
                            }
                            dishAdapter.mDishList.addAll(dishesList);
                            isRefresh= false;
                        }else{
                            dishAdapter = new DishAdapter(mContext, false);
                            dishAdapter.mDishList.addAll(dishesList);
                        }
                        dishListView.setAdapter(dishAdapter);
                        dishAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(progress.isShowing()){
                    progress.dismiss();
                }
                getLocalDishesData();
                if (dishesList != null && dishesList.size()>0) {
                    if(isRefresh){
                        if(dishAdapter.mDishList!=null && dishAdapter.mDishList.size()>0){
                            dishAdapter.mDishList.clear();
                        }
                        dishAdapter.mDishList.addAll(dishesList);
                        isRefresh= false;
                    }else{
                        dishAdapter = new DishAdapter(mContext, false);
                        dishAdapter.mDishList.addAll(dishesList);
                    }
                    dishListView.setAdapter(dishAdapter);
                    dishAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "Network connection is wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocalDishesData(){
        List<DishBean> localDishList = (List<DishBean>)ObjectPersistence.readObjectFromFile(mContext, DISHES_DATA_PATH);
        if(localDishList !=null ){
            dishesList = localDishList;
        }
    }
}