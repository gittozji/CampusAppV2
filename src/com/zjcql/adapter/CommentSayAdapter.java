package com.zjcql.adapter;

import java.util.List;

import com.zjcql.activity.CommentSayActivity;
import com.zjcql.activity.UserActivity;
import com.zjcql.adapter.SayAdapter.ViewHolder;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.bmobbean.Comment;
import com.zjcql.bmobbean.Says;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.popup.PictureShowerPopup;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;
import com.zjcql.util.TimeUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

/** 
* 吐槽墙评论
* @author phlofy
* @date 2016年1月24日 下午1:51:51 
*/
public class CommentSayAdapter extends BaseContentAdapter<Comment>{

	public CommentSayAdapter(Context context, List<Comment> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*****************************获取convertView**********************************/
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_list_comment, null);
			viewHolder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
			viewHolder.content = (TextView) convertView.findViewById(R.id.content);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.lay = (RelativeLayout) convertView.findViewById(R.id.layout_menu);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		/*******************************得到信息、发布者实体******************************/
		final Comment comment = dataList.get(position);
		final User user = comment.getUser();
		
		/*******************************给各个view添加信息******************************/
		MyBitmapUtil.displayIcon(mContext, user.getAvatar(), viewHolder.icon);
		viewHolder.content.setText(comment.getContent());
		viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(comment.getCreatedAt(), "yyyy-MM-dd HH:mm:ss")));
		
		/*******************************添加监听器******************************/
		viewHolder.lay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", user);
				in.putExtras(bundle);
				mContext.startActivity(in);
			}
		});
		return convertView;
	}
	public static class ViewHolder {
		CircleImageView icon;
		TextView content;
		TextView time;
		RelativeLayout lay;
	}

}
