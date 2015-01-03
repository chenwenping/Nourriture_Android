package team_10.nourriture_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.bean.CommentBean;

/**
 * Created by ping on 2014/12/27.
 */
public class CommentAdapter extends BaseAdapter {

    public List<CommentBean> mCommentList = new ArrayList<CommentBean>();
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;

    public CommentAdapter(Context context, List<CommentBean> commentList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCommentList = commentList;
    }

    public CommentAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder cvh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_comment, null);
            cvh = new CommentViewHolder();
            cvh.user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            cvh.user_photo = (ImageView) convertView.findViewById(R.id.img_user_photo);
            cvh.comment_content = (TextView) convertView.findViewById(R.id.tv_comment_content);
            cvh.comment_time = (TextView) convertView.findViewById(R.id.tv_comment_time);
            convertView.setTag(cvh);
        } else {
            cvh = (CommentViewHolder) convertView.getTag();
        }

        final CommentBean commentBean = (CommentBean) mCommentList.get(position);
//        cvh.user_name.setText(commentBean.getUserBean().getn);
//        cvh.user_photo.setImageResource();
//        UserBean userBean = commentBean.getUserBean();
//        UserBean userBean = getUser();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(commentBean.getDate());
        cvh.comment_time.setText(strDate);

        cvh.comment_content.setText(commentBean.getContent());
        cvh.comment_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    static class CommentViewHolder {
        TextView user_name;
        TextView comment_content;
        TextView comment_time;
        ImageView user_photo;
    }
}
