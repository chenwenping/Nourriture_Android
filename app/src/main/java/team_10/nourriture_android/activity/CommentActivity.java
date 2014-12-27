package team_10.nourriture_android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.CommentAdapter;
import team_10.nourriture_android.bean.CommentBean;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;

/**
 * Created by ping on 2014/12/21.
 */
public class CommentActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private DishBean dishBean;
    private List<CommentBean> commentList;
    private Context mContext;

    private CommentAdapter commentAdapter;
    private SwipeRefreshLayout swipeLayout;
    private ListView commentListView;
    private TextView no_comment_tv;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mContext = this;
        Intent intent = getIntent();
        dishBean= (DishBean)intent.getSerializableExtra("dishBean");

        initView();
        getAllComments();
    }

    public void initView(){
        swipeLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        commentListView = (ListView)this.findViewById(R.id.commentListView);
        no_comment_tv = (TextView)this.findViewById(R.id.tv_no_dish_comment);
    }

    @Override
    public void onRefresh() {
        if(!isRefresh){
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    getAllComments();
                }
            }, 3000);
        }
    }

    public void getAllComments(){
        String url = "getCommentsFromDish/" + dishBean.get_id();
        Log.e("url", url);
        NourritureRestClient.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //super.onSuccess(statusCode, headers, response);
                try {
                    commentList = JsonTobean.getList(CommentBean[].class, response.toString());
                    Log.e("commentList", response.toString());
                    if(commentList==null || commentList.size()==0){
                        no_comment_tv.setVisibility(View.VISIBLE);
                        swipeLayout.setVisibility(View.GONE);
                    } else {
                        no_comment_tv.setVisibility(View.GONE);
                        swipeLayout.setVisibility(View.VISIBLE);
                        if (isRefresh) {
                            commentAdapter.mCommentList.clear();
                            commentAdapter.mCommentList.addAll(commentList);
                            isRefresh = false;
                        } else {
                            commentAdapter = new CommentAdapter(mContext, false);
                            commentAdapter.mCommentList.addAll(commentList);
                        }
                        commentListView.setAdapter(commentAdapter);
                        commentAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}
