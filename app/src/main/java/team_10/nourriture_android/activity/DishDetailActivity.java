package team_10.nourriture_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.bean.LikeBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.AsynImageLoader;

/**
 * Created by ping on 2014/12/21.
 */
public class DishDetailActivity extends ActionBarActivity implements View.OnClickListener{

    private DishBean dishBean;
    private AsynImageLoader asynImageLoader;

    private Button back_btn;
    private ImageView dish_picture_img, dish_favor_img;
    private LinearLayout dish_favor_ll, dish_comment_ll;
    private TextView dish_name_tv, dish_description_tv, dish_ingredient_tv, dish_step_tv;

    private List<LikeBean> likeBeanList;
    private LikeBean likeBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        Intent intent = getIntent();
        dishBean= (DishBean)intent.getSerializableExtra("dishBean");

        initView();
        initData();
    }

    public void initView(){
        dish_name_tv = (TextView)this.findViewById(R.id.tv_dish_name);
        dish_description_tv = (TextView)this.findViewById(R.id.tv_dish_description);
        dish_ingredient_tv = (TextView)this.findViewById(R.id.tv_dish_ingredient);
        dish_step_tv = (TextView)this.findViewById(R.id.tv_dish_step);
        dish_picture_img = (ImageView)this.findViewById(R.id.img_dish_picture);
        dish_favor_img = (ImageView)this.findViewById(R.id.img_dish_favor);
        dish_favor_ll = (LinearLayout)this.findViewById(R.id.ll_dish_favor);
        dish_comment_ll = (LinearLayout)this.findViewById(R.id.ll_dish_comment);
        back_btn = (Button)this.findViewById(R.id.btn_back);

        back_btn.setOnClickListener(this);
        dish_favor_ll.setOnClickListener(this);
        dish_comment_ll.setOnClickListener(this);
    }

    public void initData(){
        dish_name_tv.setText(dishBean.getName());
        dish_description_tv.setText(dishBean.getDescription());

        asynImageLoader = new AsynImageLoader();
        if(dishBean.getPicture()==null || "".equals(dishBean.getPicture().trim()) || "null".equals(dishBean.getPicture().trim())){
            dish_picture_img.setImageResource(R.drawable.default_dish_picture);
        }else{
            asynImageLoader.showImageAsyn(dish_picture_img, dishBean.getPicture(), R.drawable.default_dish_picture);
        }

        getLikesFromDish();
    }

    public void getLikesFromDish(){
        String url = "/getLikesFromDish/" + dishBean.get_id();
        NourritureRestClient.get(url, null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(statusCode == 200){
                    try {
                        likeBean = JsonTobean.getBean(LikeBean.class, response.toString());
                        Log.e("likeBean", response.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    // http request error
                }
//                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
               // super.onSuccess(statusCode, headers, responseString);
                if(statusCode == 200){
                    try {
                        likeBeanList = JsonTobean.getList(LikeBean[].class, responseString.toString());
                        Log.e("likeBeanList", responseString.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    // http request error
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
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_dish_favor:
                dish_favor_img.setImageResource(R.drawable.favor_btn_highlight);
                Toast.makeText(this, "like it.", Toast.LENGTH_SHORT).show();
                // other actions

                break;
            case R.id.ll_dish_comment:
                Intent intent = new Intent(DishDetailActivity.this, DishCommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dishBean", dishBean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
}
