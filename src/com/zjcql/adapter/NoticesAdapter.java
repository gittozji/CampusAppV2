package com.zjcql.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Notices;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.SelectColorDialog;
import com.zjcql.popup.ShowDetailNoticePopup;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.TimeUtil;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/** 
* 公告item
* @author phlofy
* @date 2015年10月17日 下午2:50:56 
*/
public class NoticesAdapter extends BaseContentAdapter<Notices>{

	public NoticesAdapter(Context context, List<Notices> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*****************************获取convertView**********************************/
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_list_notice, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		/*******************************得到通知、发布者实体******************************/
		final Notices mNotices = dataList.get(position);
		User user = mNotices.getUser();
		
		/*******************************给各个view添加信息******************************/
		if(mNotices.getFile() != null)
			MyBitmapUtil.displayImage(mContext, mNotices.getFile(), viewHolder.image);
		else
			viewHolder.image.setImageResource(R.drawable.user_back_small);
		viewHolder.title.setTextColor(Color.parseColor(SelectColorDialog.colors[mNotices.getIndex()]));
		viewHolder.title.setText(mNotices.getTitle());
		viewHolder.name.setText(user.getNick());
		viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(mNotices.getCreatedAt(), "yyyy-MM-dd HH:mm:ss")));
		
		/**********************************添加监听器**********************************/
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new ShowDetailNoticePopup(mContext, viewHolder.name,mNotices).show();
			}
		});
		
		return convertView;
	}
	public static class ViewHolder {
		ImageView image;
		TextView title;
		TextView name;
		TextView time;
	}
}
