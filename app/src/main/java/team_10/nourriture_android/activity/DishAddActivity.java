package team_10.nourriture_android.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.RequestParams;

import team_10.nourriture_android.R;

/**
 * Created by ping on 2014/12/21.
 */
public class DishAddActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText dish_name_et;
    private EditText dish_description_et;
    private Button dish_add_btn;
    private ImageView dish_picture;
    private Button back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_add);

        initView();
    }

    public void initView(){
        dish_name_et = (EditText)this.findViewById(R.id.et_dish_name);
        dish_description_et = (EditText)this.findViewById(R.id.et_dish_description);
        dish_add_btn = (Button)this.findViewById(R.id.btn_dish_add);
        dish_picture = (ImageView)this.findViewById(R.id.img_dish_picture);
        back_btn = (Button)this.findViewById(R.id.btn_back);

        dish_add_btn.setOnClickListener(this);
        dish_picture.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    public void addDish(){
        RequestParams params = new RequestParams();
        params.put("key", "value");
        params.put("more", "data");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_dish_add:
                addDish();
                break;
            case R.id.img_dish_picture:
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
}
