package com.zjcql.adapter;

import java.util.List;

import com.zjcql.activity.CommentSayActivity;
import com.zjcql.activity.UserActivity;
import com.zjcql.activity.ReplyAnswerActiviy;
import com.zjcql.adapter.SayAdapter.ViewHolder;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.bmobbean.Answer;
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
import android.widget.LinearLayout;

/** 
* 问答adapter
* @author phlofy
* @date 2016年1月25日 下午10:19:08 
*/
public class AnswerAdapter extends BaseContentAdapter<Answer>{

	public AnswerAdapter(Context context, List<Answer> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*****************************获取convertView**********************************/
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_answer, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.lay = (LinearLayout) convertView.findViewById(R.id.layout_menu);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		/*******************************得到信息、发布者实体******************************/
		final Answer answer = dataList.get(position);
		User user = answer.getFromUser();
		
		/*******************************给各个view添加信息******************************/
		viewHolder.title.setText(answer.getTitle());
		viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(answer.getCreatedAt(), "yyyy-MM-dd HH:mm:ss")));
		
		/*******************************添加监听器******************************/
		viewHolder.lay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,ReplyAnswerActiviy.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("answer", answer);
				in.putExtras(bundle);
				mContext.startActivity(in);
				
			}
		});
		return convertView;
	}
	public static class ViewHolder {
		TextView title;
		TextView time;
		LinearLayout lay;
	}


}
