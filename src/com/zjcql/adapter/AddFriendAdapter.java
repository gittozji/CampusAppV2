package com.zjcql.adapter;

import java.util.List;

import com.zjcql.activity.UserActivity;
import com.zjcql.activity.NavigationActivity;
import com.zjcql.adapter.NoticesAdapter.ViewHolder;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.campusappv2.R;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

/** 
* 添加好友Item
* @author phlofy
* @date 2015年12月5日 下午10:30:29 
*/
public class AddFriendAdapter extends BaseContentAdapter<BmobChatUser>{

	public AddFriendAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*****************************获取convertView**********************************/
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_list_add_user, null);
			viewHolder.rl = (RelativeLayout) convertView.findViewById(R.id.users_show_layout);
			viewHolder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
			viewHolder.nick = (TextView) convertView.findViewById(R.id.name);
			viewHolder.id = (TextView) convertView.findViewById(R.id.text);
			viewHolder.add = (Button) convertView.findViewById(R.id.add);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		/*******************************用户实体******************************/
		final BmobChatUser mBmobChatUser = dataList.get(position);
		/*******************************给各个view添加信息******************************/
		MyBitmapUtil.displayIcon(mContext, mBmobChatUser.getAvatar(), viewHolder.icon);
		viewHolder.nick.setText(mBmobChatUser.getNick());
		viewHolder.id.setText(mBmobChatUser.getObjectId());
		/*******************************设置监听器******************************/
		viewHolder.add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
            	// 添加好友
            	BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, mBmobChatUser.getObjectId(),new PushListener() {
    		        @Override
    		        public void onSuccess() {
    		            toast("发送请求成功，等待对方验证!");
    		        }
    		        @Override
    		        public void onFailure(int arg0, final String arg1) {
    		            toast("发送请求失败，请重新添加!");
    		        }
    		    });
			}
		});
		viewHolder.rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", mBmobChatUser);
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
		TextView id;
		Button add;
	}
}
