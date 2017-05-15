package com.zjcql.adapter;

import java.util.List;

import com.zjcql.activity.CommentSayActivity;
import com.zjcql.activity.UserActivity;
import com.zjcql.adapter.NoticesAdapter.ViewHolder;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.bmobbean.Notices;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/** 
* 吐槽墙Adapter
* @author phlofy
* @date 2016年1月13日 上午11:14:34 
*/
public class SayAdapter extends BaseContentAdapter<Says>{

	public SayAdapter(Context context, List<Says> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*****************************获取convertView**********************************/
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_list_says, null);
			viewHolder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
			viewHolder.content = (TextView) convertView.findViewById(R.id.content);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.grid = (GridView) convertView.findViewById(R.id.grid);
			viewHolder.lay = convertView.findViewById(R.id.layout_menu);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		/*******************************得到信息、发布者实体******************************/
		final Says says = dataList.get(position);
		User user = says.getFromUser();
		
		/*******************************给各个view添加信息******************************/
		MyBitmapUtil.displayIcon(mContext, user.getAvatar(), viewHolder.icon);
		viewHolder.content.setText(says.getContent());
		viewHolder.name.setText(user.getNick());
		viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(says.getCreatedAt(), "yyyy-MM-dd HH:mm:ss")));
		
		BaseAdapter mAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// 获取屏幕的宽度，用于动态控制三个image的大小
				int mScreenWidth = MyUtil.getScreenWidth(mContext) - MyUtil.dip2px(mContext,26);
				AbsListView.LayoutParams mLayoutParams =  new AbsListView.LayoutParams(mScreenWidth/3, mScreenWidth/3);
				ImageView mImageView = new ImageView(mContext);
				mImageView.setScaleType(ScaleType.CENTER_CROP);
				mImageView.setLayoutParams(mLayoutParams);
				MyBitmapUtil.displayImage(mContext, says.getFiles()[position], mImageView);
				return mImageView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				if(says.getFiles() == null)
					return 0;
				return says.getFiles().length;
			}
		};
		viewHolder.grid.setAdapter(mAdapter);
		
		/*******************************添加监听器******************************/
		viewHolder.icon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", says.getFromUser());
				in.putExtras(bundle);
				mContext.startActivity(in);
			}
		});
		viewHolder.lay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,CommentSayActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("says", says);
				in.putExtras(bundle);
				mContext.startActivity(in);
				
			}
		});
		viewHolder.grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PictureShowerPopup psp = new PictureShowerPopup(mContext, viewHolder.grid, says.getFiles()[position]);
				psp.show();
			}
		});
		return convertView;
	}
	public static class ViewHolder {
		CircleImageView icon;
		TextView content;
		TextView name;
		TextView time;
		GridView grid;
		View lay;
	}

}
