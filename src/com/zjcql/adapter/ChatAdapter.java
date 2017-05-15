package com.zjcql.adapter;

import java.util.List;

import com.zjcql.activity.UserActivity;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.campusappv2.R;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.TimeUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

/** 
* 聊天Adapter
* @author phlofy
* @date 2015年12月20日 下午3:17:07 
*/
public class ChatAdapter extends BaseContentAdapter<BmobMsg>{

	//4种Item的类型
	//文本
	private final int TYPE_RECEIVER_TXT = 0;
	private final int TYPE_SEND_TXT = 1;
	//图片
	private final int TYPE_SEND_IMAGE = 2;
	private final int TYPE_RECEIVER_IMAGE = 3;
	
	BmobChatUser targetUser;
	String currentObjectId = "";

	public ChatAdapter(Context context, List<BmobMsg> list,BmobChatUser targetUser) {
		super(context, list);
		currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
		this.targetUser = targetUser;
	}
	
	@Override
	public int getItemViewType(int position) {
		BmobMsg msg = dataList.get(position);
		switch(msg.getMsgType()){
		case BmobConfig.TYPE_IMAGE:{
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
		}
		case BmobConfig.TYPE_TEXT:{
			return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
		}
		}
		return 0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 4;
	}
	
	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		convertView = createViewByType(dataList.get(position), position);
		convertView.findViewById(R.id.root_layout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		return convertView;
	}
	
	
	private View createViewByType(final BmobMsg message, int position) {
		View root = null;
		int type = message.getMsgType();
		switch(type){
		// 图片消息
		case BmobConfig.TYPE_IMAGE:{
			return root;
		}
		//文本消息
		case BmobConfig.TYPE_TEXT:{
			// 发送消息View
			if(getItemViewType(position) == TYPE_SEND_TXT){
				root = mInflater.inflate(R.layout.item_chat_sent_message, null);
				TextView time = (TextView) root.findViewById(R.id.time);
				CircleImageView icon = (CircleImageView) root.findViewById(R.id.icon);
				TextView text = (TextView) root.findViewById(R.id.text);
				ProgressBar progress = (ProgressBar) root.findViewById(R.id.progress);
				ImageView hint = (ImageView) root.findViewById(R.id.answer);
				
				time.setText(TimeUtil.getChatTime(Long.parseLong(message.getMsgTime())));
				MyBitmapUtil.displayIcon(mContext, message.getBelongAvatar(), icon);
				text.setText(message.getContent());
				System.out.println(message.getStatus()+"enen"+BmobConfig.STATUS_SEND_RECEIVERED);
				switch(message.getStatus()){
				case BmobConfig.STATUS_SEND_FAIL:
					System.out.println("发送失败");
					hint.setVisibility(View.VISIBLE);
					progress.setVisibility(View.GONE);
					
					hint.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							message.setStatus(BmobConfig.STATUS_SEND_START);
							ChatAdapter.this.notifyDataSetChanged();
							BmobChatManager.getInstance(mContext).resendTextMessage(targetUser, message, new PushListener() {
								
								@Override
								public void onSuccess() {
									message.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
									ChatAdapter.this.notifyDataSetChanged();
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									message.setStatus(BmobConfig.STATUS_SEND_FAIL);
									ChatAdapter.this.notifyDataSetChanged();
								}
							});
						}
					});
					break;
				case BmobConfig.STATUS_SEND_SUCCESS:
					System.out.println("成功发送到服务器");
					hint.setVisibility(View.GONE);
					progress.setVisibility(View.GONE);
					break;
				case BmobConfig.STATUS_SEND_RECEIVERED:
					System.out.println("已经送达");
					hint.setVisibility(View.GONE);
					progress.setVisibility(View.GONE);
					break;
				case BmobConfig.STATUS_SEND_START:
					System.out.println("正在发送");
					hint.setVisibility(View.GONE);
					progress.setVisibility(View.VISIBLE);
					break;
				}
			}
			else{
				// 接收消息View
				root = mInflater.inflate(R.layout.item_chat_received_message, null);
				// 文本消息
				
				TextView time = (TextView) root.findViewById(R.id.time);
				CircleImageView icon = (CircleImageView) root.findViewById(R.id.icon);
				TextView text = (TextView) root.findViewById(R.id.text);
				
				time.setText(TimeUtil.getChatTime(Long.parseLong(message.getMsgTime())));
				MyBitmapUtil.displayIcon(mContext, message.getBelongAvatar(), icon);
				text.setText(message.getContent());
				
				icon.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent in = new Intent(mContext,UserActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("user", targetUser);
						in.putExtras(bundle);
						mContext.startActivity(in);
					}
				});
			}
			return root;
		}
		}
		return root;
	}
}
