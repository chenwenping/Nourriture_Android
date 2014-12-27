package team_10.nourriture_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import team_10.nourriture_android.R;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.utils.AsynImageLoader;

/**
 * Created by ping on 2014/12/21.
 */
public class DishDetailActivity extends ActionBarActivity implements View.OnClickListener{

    private DishBean dishBean;
    private  AsynImageLoader asynImageLoader;

    private Button back_btn;
    private ImageView dish_picture_img, dish_favor_img;
    private LinearLayout dish_favor_ll, dish_comment_ll;
    private TextView dish_name_tv, dish_description_tv, dish_ingredient_tv, dish_step_tv;

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

//        dish_ingredient_tv.setText(dishBean.getIngredientBean().getName());
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
                Intent intent = new Intent(DishDetailActivity.this, CommentActivity.class);
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
