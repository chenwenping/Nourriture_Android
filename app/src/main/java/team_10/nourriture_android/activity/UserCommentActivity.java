package team_10.nourriture_android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.adapter.CommentAdapter;
import team_10.nourriture_android.application.MyApplication;
import team_10.nourriture_android.bean.CommentBean;
import team_10.nourriture_android.bean.UserBean;
import team_10.nourriture_android.jsonTobean.JsonTobean;
import team_10.nourriture_android.utils.ObjectPersistence;

/**
 * Created by ping on 2014/12/28.
 */
public class UserCommentActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private UserBean userBean;
    private List<CommentBean> commentList;
    private List<CommentBean> userCommentList;
    private Context mContext;
    private Button back_btn;
    private CommentAdapter commentAdapter;
    private SwipeRefreshLayout swipeLayout;
    private ListView commentListView;
    private boolean isRefresh = false;
    private ProgressDialog progress;

    private static final String USER_COMMENTS_DATA_PATH="_user_comments_data.bean";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comment);

        initView();

        mContext = this;
        userBean = MyApplication.getInstance().getUserBeanFromFile();

        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.show();

        getUserCommentList();
    }

    public void initView(){
        swipeLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        commentListView = (ListView)this.findViewById(R.id.commentListView);
        commentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CommentBean commentBean = (CommentBean)commentAdapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Confirm delete");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Do you really want to delete the comment ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete the comment.
                        progress.setMessage("Deleting...");
                        progress.show();
                        deleteUserComment(commentBean);
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
                return true;
            }
        });

        back_btn = (Button)this.findViewById(R.id.btn_back);
        back_btn.setOnClickListener(this);
    }

    public void getUserCommentList(){
        NourritureRestClient.get("comments", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("commentList", response.toString());
                if(progress.isShowing()){
                    progress.dismiss();
                }
                if(statusCode == 200) {
                    try {
                        commentList = JsonTobean.getList(CommentBean[].class, response.toString());
                        userCommentList = new ArrayList<>();
                        if(commentList!=null && commentList.size()>0){
                            for(int i=0; i<commentList.size(); i++){
                                CommentBean commentBean = commentList.get(i);
                                if((commentBean.getUser()).equals(userBean.get_id())){
                                    userCommentList.add(commentBean);
                                }
                            }
                        }
                        Collections.reverse(userCommentList);
                        ObjectPersistence.writeObjectToFile(mContext, userCommentList, USER_COMMENTS_DATA_PATH);
                        if (isRefresh) {
                            if(commentAdapter.mCommentList!=null && commentAdapter.mCommentList.size()>0){
                                commentAdapter.mCommentList.clear();
                            }
                            commentAdapter.mCommentList.addAll(userCommentList);
                            isRefresh = false;
                        } else {
                            commentAdapter = new CommentAdapter(mContext, false);
                            commentAdapter.mCommentList.addAll(userCommentList);
                        }
                        commentListView.setAdapter(commentAdapter);
                        commentAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    getLocalCommentsData();
                    if (isRefresh) {
                        if(commentAdapter.mCommentList!=null && commentAdapter.mCommentList.size()>0){
                            commentAdapter.mCommentList.clear();
                        }
                        commentAdapter.mCommentList.addAll(userCommentList);
                        isRefresh = false;
                    } else {
                        commentAdapter = new CommentAdapter(mContext, false);
                        commentAdapter.mCommentList.addAll(userCommentList);
                    }
                    commentListView.setAdapter(commentAdapter);
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(progress.isShowing()){
                    progress.dismiss();
                }
                getLocalCommentsData();
                if (isRefresh) {
                    if(commentAdapter.mCommentList!=null && commentAdapter.mCommentList.size()>0){
                        commentAdapter.mCommentList.clear();
                    }
                    commentAdapter.mCommentList.addAll(userCommentList);
                    isRefresh = false;
                } else {
                    commentAdapter = new CommentAdapter(mContext, false);
                    commentAdapter.mCommentList.addAll(userCommentList);
                }
                commentListView.setAdapter(commentAdapter);
                commentAdapter.notifyDataSetChanged();
            }
        });
    }

    public void deleteUserComment(CommentBean commentBean){
        String url = "comments/" + commentBean.get_id();
        NourritureRestClient.delete(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("delete comment", response.toString());
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                if (statusCode == 204) {
                    getUserCommentList();
                } else {
                    Toast.makeText(UserCommentActivity.this, "Deleting comment is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                Toast.makeText(UserCommentActivity.this, "Deleting comment is wrong. Please try it again.", Toast.LENGTH_SHORT).show();
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
                    getUserCommentList();
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

    private void getLocalCommentsData(){
        List<CommentBean> localCommentList = (List<CommentBean>) ObjectPersistence.readObjectFromFile(mContext, USER_COMMENTS_DATA_PATH);
        if(localCommentList !=null ){
            commentList = localCommentList;
        }
    }
}
