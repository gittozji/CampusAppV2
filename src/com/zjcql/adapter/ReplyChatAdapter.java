package com.zjcql.adapter;

import java.util.List;

import com.zjcql.activity.ChatActivity;
import com.zjcql.activity.UserActivity;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.FindListener;

/** 
* 回话Adapter
* @author phlofy
* @date 2016年1月2日 下午2:30:49 
*/
public class ReplyChatAdapter extends BaseContentAdapter<BmobRecent>{

	public ReplyChatAdapter(Context context, List<BmobRecent> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(final int position, View convertView, ViewGroup parent) {
		/**********************************获取View*********************************/
		RelativeLayout ray;
		CircleImageView icon;
		TextView name;
		ImageView hasNew;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_reply_list, null);
		}
		icon = (CircleImageView) convertView.findViewById(R.id.icon);
		name = (TextView) convertView.findViewById(R.id.name);
		hasNew = (ImageView) convertView.findViewById(R.id.has_new);
		ray = (RelativeLayout) convertView.findViewById(R.id.users_show_layout);
		
		/*********************************填充数据*********************************/
		final BmobRecent item = dataList.get(position);
		MyBitmapUtil.displayIcon(mContext, item.getAvatar(), icon);
		name.setText(item.getNick()+"");
		if(BmobDB.create(mContext).getUnreadCount(item.getTargetid()) > 0){
			hasNew.setVisibility(View.VISIBLE);
		}
		else{
			hasNew.setVisibility(View.GONE);
		}
		
		/*********************************点击监听*********************************/
		ray.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BmobQuery<User> query = new BmobQuery<User>();
				query.addWhereEqualTo("objectId", item.getTargetid());
				//判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
				boolean isCache = query.hasCachedResult(mContext,User.class);
				if(isCache){//此为举个例子，并不一定按这种方式来设置缓存策略
				    query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
				}
				else{
				    query.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
				}
				System.out.println("is finding  "+isCache);
				query.findObjects(mContext, new FindListener<User>() {
					
					@Override
					public void onSuccess(List<User> arg0) {
						Intent intent = new Intent(mContext, ChatActivity.class);
						intent.putExtra("user", arg0.get(0));
						mContext.startActivity(intent);
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						toast("unknow");
					}
				});
			}
		});
		ray.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				EditDialog editDialog = new EditDialog(mContext,"是否删除？",false) {
					
					@Override
					public void onClickEventForButton2(String text) {
						BmobDB.create(mContext).deleteRecent(item.getTargetid());
						BmobDB.create(mContext).deleteMessages(item.getTargetid());
						ReplyChatAdapter.this.dataList.remove(position);
						notifyDataSetChanged();
					}
					
					@Override
					public void onClickEventForButton1() {
					}
				};
				editDialog.show();
				return true;
			}
		});
		return convertView;
	}
	
}
