package com.zjcql.adapter;

import java.util.List;

import com.zjcql.activity.UserActivity;
import com.zjcql.adapter.FriendListAdapter.ViewHolder;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.CollectionUtils;
import com.zjcql.util.MyBitmapUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/** 
* 好友列表adapter
* @author phlofy
* @date 2015年12月19日 下午5:19:18 
*/
public class FriendListAdapter extends BaseContentAdapter<BmobChatUser>{
	
	public FriendListAdapter(Context context,List<BmobChatUser> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*****************************获取convertView**********************************/
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_friends_list, null);
			viewHolder.rl = (RelativeLayout) convertView.findViewById(R.id.users_show_layout);
			viewHolder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
			viewHolder.nick = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		/*******************************获取操作实体******************************/
		final BmobChatUser msg = dataList.get(position);
		/*******************************给各个view添加信息******************************/
		MyBitmapUtil.displayIcon(mContext, msg.getAvatar(), viewHolder.icon);
		viewHolder.nick.setText(msg.getNick());
		/*******************************设置监听器******************************/
		
		viewHolder.rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 跳转到用户界面
				Intent in = new Intent(mContext,UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("objectId", msg.getObjectId());
				in.putExtras(bundle);
				mContext.startActivity(in);
			}
		});
		return convertView;
	}
	public static class ViewHolder {
		RelativeLayout rl;
		CircleImageView icon;
		TextView nick;
	}
}
