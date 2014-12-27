package team_10.nourriture_android.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import team_10.nourriture_android.R;

/**
 * Created by ping on 2014/12/22.
 */
public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    public static final String TAG_USER_ACCOUNT = "user.s";

    private EditText uername_et;
    private EditText password_et;
    private Button login_btn;
    private Button back_btn;
    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView(){
        uername_et = (EditText)this.findViewById(R.id.et_username);
        password_et = (EditText)this.findViewById(R.id.et_password);
        login_btn = (Button)this.findViewById(R.id.btn_login);
        back_btn = (Button)this.findViewById(R.id.btn_back);
        login_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    public void checkLogin(){
        userName = uername_et.getText().toString().trim();
        password = password_et.getText().toString().trim();
        if(userName==null || "".equals(userName)){
            Toast.makeText(this, "Please enter the username.", Toast.LENGTH_SHORT).show();
            uername_et.setFocusable(true);
        }else if(password==null || "".equals(password)){
            Toast.makeText(this, "Please enter the password.", Toast.LENGTH_SHORT).show();
            password_et.setFocusable(true);
        }else {
            login();
        }
    }

    public void login(){
        String str = "Basic " + userName + password;
        NourritureRestClient.get("login", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("login", response.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                checkLogin();
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
