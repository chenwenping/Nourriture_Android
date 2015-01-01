package team_10.nourriture_android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import team_10.nourriture_android.R;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.utils.GlobalParams;
import team_10.nourriture_android.utils.SharedPreferencesUtil;


/**
 * Created by ping on 2014/12/20.
 */
public class NotificationFragment extends Fragment {

    private LinearLayout ll_dishes_comment, ll_favor_dishes, ll_my_friends;

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

        ll_dishes_comment = (LinearLayout)getActivity().findViewById(R.id.ll_dishes_comment);
        ll_favor_dishes = (LinearLayout)getActivity().findViewById(R.id.ll_favor_dishes);
        ll_my_friends = (LinearLayout)getActivity().findViewById(R.id.ll_my_friends);

        ll_dishes_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin){
                    Intent intent = new Intent(getActivity().getApplicationContext(), UserCommentActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, request);
                }
            }
        });

        ll_favor_dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin){
                    Intent intent = new Intent(getActivity().getApplicationContext(), FavorDishesActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, request);
                }
            }
        });

        ll_my_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin){
                    Intent intent = new Intent(getActivity().getApplicationContext(), FriendsActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, request);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==LoginActivity.KEY_IS_LOGIN){
            isLogin = true;
        }
    }
}
