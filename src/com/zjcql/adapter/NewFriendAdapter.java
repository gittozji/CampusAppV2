package com.zjcql.adapter;

import java.util.List;

import com.zjcql.activity.UserActivity;
import com.zjcql.adapter.AddFriendAdapter.ViewHolder;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.base.MyApplication;
import com.zjcql.campusappv2.R;
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
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.UpdateListener;

/** 
* 添加好友adpter
* @author phlofy
* @date 2015年12月12日 下午7:38:54 
*/
public class NewFriendAdapter extends BaseContentAdapter<BmobInvitation>{
	
	public NewFriendAdapter(Context context,List<BmobInvitation> list) {
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
		/*******************************获取操作实体******************************/
		final BmobInvitation msg = dataList.get(position);
		/*******************************给各个view添加信息******************************/
		MyBitmapUtil.displayIcon(mContext, msg.getAvatar(), viewHolder.icon);
		int status = msg.getStatus();
		if(status==BmobConfig.INVITE_ADD_NO_VALIDATION||status==BmobConfig.INVITE_ADD_NO_VALI_RECEIVED){
			viewHolder.add.setTextColor(mContext.getResources().getColor(R.color.primary_dark));
			viewHolder.add.setText("添加");
			viewHolder.add.setEnabled(true);
			viewHolder.add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					BmobLog.i("点击同意按钮:"+msg.getFromid());
					agressAdd(viewHolder.add, msg);
				}
			});
		}
		else if(status==BmobConfig.INVITE_ADD_AGREE){
			viewHolder.add.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
			viewHolder.add.setText("已添加");
			viewHolder.add.setEnabled(false);
		}
		viewHolder.nick.setText(msg.getNick());
		viewHolder.id.setText(msg.getFromid());
		/*******************************设置监听器******************************/
		
		viewHolder.rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("objectId", msg.getFromid());
				in.putExtras(bundle);
				mContext.startActivity(in);
			}
		});
		return convertView;
	}
	/**添加好友
	  * agressAdd
	  * @Title: agressAdd
	  * @Description: TODO
	  * @param @param btn_add
	  * @param @param msg 
	  * @return void
	  * @throws
	  */
	private void agressAdd(final Button btn_add,final BmobInvitation msg){
		final ProgressDialog progress = new ProgressDialog(mContext);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		try {
			//同意添加好友
			BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					progress.dismiss();
					btn_add.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
					btn_add.setText("已添加");
					btn_add.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
					btn_add.setEnabled(false);
					msg.setStatus(BmobConfig.INVITE_ADD_AGREE);
					//保存到application中方便比较
					MyApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));	
				}
				
				@Override
				public void onFailure(int arg0, final String arg1) {
					// TODO Auto-generated method stub
					progress.dismiss();
					toast("添加失败: " +arg1);
				}
			});
		} catch (final Exception e) {
			progress.dismiss();
			toast("添加失败: " +e.getMessage());
		}
	}
	public static class ViewHolder {
		RelativeLayout rl;
		CircleImageView icon;
		TextView nick;
		TextView id;
		Button add;
	}
}
