package team_10.nourriture_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import team_10.nourriture_android.R;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.utils.AsynImageLoader;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;


/**
 * Created by ping on 2014/12/20.
 */
public class NotificationFragment extends Fragment implements View.OnClickListener{

    private RelativeLayout rl_user_login, rl_user_info;
    private Button btn_login;
    private TextView tv_name, tv_birth, tv_introduction;
    private ImageView img_photo;
    private LinearLayout ll_dishes_comment, ll_favor_dishes, ll_my_friends;
    private UserBean userBean;
    private boolean isLogin = false;
    private SharedPreferences sp;
    private int request = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //isLogin = MyApplication.getInstance().isLogin();
        sp = getActivity().getSharedPreferences(GlobalParams.TAG_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        isLogin = sp.getBoolean(SharedPreferencesUtil.TAG_IS_LOGIN, false);
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rl_user_login = (RelativeLayout)getActivity().findViewById(R.id.user_login_rl);
        rl_user_info = (RelativeLayout)getActivity().findViewById(R.id.user_info_rl);
        btn_login = (Button)getActivity().findViewById(R.id.login_btn);
        img_photo = (ImageView)getActivity().findViewById(R.id.photo_img);
        tv_name = (TextView)getActivity().findViewById(R.id.name_tv);
        tv_birth = (TextView)getActivity().findViewById(R.id.birth_tv);
        tv_introduction = (TextView)getActivity().findViewById(R.id.introduction_tv);

        if(isLogin){
            rl_user_login.setVisibility(View.GONE);
            rl_user_info.setVisibility(View.VISIBLE);

            userBean = MyApplication.getInstance().getUserBeanFromFile();
            Log.e("userBean", userBean.getUsername());
            tv_name.setText(userBean.getUsername());
            tv_introduction.setText(userBean.getIntroduction());
            AsynImageLoader asynImageLoader = new AsynImageLoader();
            if(userBean.getPicture()==null || "".equals(userBean.getPicture().trim()) || "null".equals(userBean.getPicture().trim())){
                img_photo.setImageResource(R.drawable.default_avatar);
            }else{
                asynImageLoader.showImageAsyn(img_photo, userBean.getPicture(), R.drawable.default_avatar);
            }
        }else{
            rl_user_info.setVisibility(View.GONE);
            rl_user_login.setVisibility(View.VISIBLE);
        }

        ll_dishes_comment = (LinearLayout)getActivity().findViewById(R.id.ll_dishes_comment);
        ll_favor_dishes = (LinearLayout)getActivity().findViewById(R.id.ll_favor_dishes);
        ll_my_friends = (LinearLayout)getActivity().findViewById(R.id.ll_my_friends);

        btn_login.setOnClickListener(this);
        ll_dishes_comment.setOnClickListener(this);
        ll_favor_dishes.setOnClickListener(this);
        ll_my_friends.setOnClickListener(this);

        notifications();
        getMyNotifications();
        getMyUnreadNotifications();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, request);
                break;
            case R.id.ll_dishes_comment:
                if(isLogin){
                    Intent intent1 = new Intent(getActivity().getApplicationContext(), UserCommentActivity.class);
                    startActivity(intent1);
                }else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent1, request);
                }
                break;
            case R.id.ll_favor_dishes:
                if(isLogin){
                    Intent intent2 = new Intent(getActivity().getApplicationContext(), FavorDishesActivity.class);
                    startActivity(intent2);
                }else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent2, request);
                }
                break;
            case R.id.ll_my_friends:
                if(isLogin){
                    Intent intent3 = new Intent(getActivity().getApplicationContext(), FriendsActivity.class);
                    startActivity(intent3);
                }else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent3, request);
                }
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

            rl_user_login.setVisibility(View.GONE);
            rl_user_info.setVisibility(View.VISIBLE);
            userBean = MyApplication.getInstance().getUserBeanFromFile();
            Log.e("userBean", userBean.getUsername());
            tv_name.setText(userBean.getUsername());
            tv_introduction.setText(userBean.getIntroduction());
            AsynImageLoader asynImageLoader = new AsynImageLoader();
            if(userBean.getPicture()==null || "".equals(userBean.getPicture().trim()) || "null".equals(userBean.getPicture().trim())){
                img_photo.setImageResource(R.drawable.default_avatar);
            }else{
                asynImageLoader.showImageAsyn(img_photo, userBean.getPicture(), R.drawable.default_avatar);
            }
        }
    }

    private void notifications(){
        NourritureRestClient.get("notifications", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("notifications", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getMyNotifications(){
        String userName = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
        String str = userName + ":" + password;
        String encodeStr = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        String loginStr = "Basic " + encodeStr;
        NourritureRestClient.addHeader(loginStr);
        NourritureRestClient.get("getMyNotifications", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("getMyNotifications", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getMyUnreadNotifications(){
        String userName = sp.getString(SharedPreferencesUtil.TAG_USER_NAME, "");
        String password = sp.getString(SharedPreferencesUtil.TAG_PASSWORD, "");
        String str = userName + ":" + password;
        String encodeStr = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        String loginStr = "Basic " + encodeStr;
        NourritureRestClient.addHeader(loginStr);
        NourritureRestClient.get("getMyUnreadNotifications", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("getMyUnreadNotifications", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
