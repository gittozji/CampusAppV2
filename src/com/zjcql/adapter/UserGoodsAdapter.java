package com.zjcql.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.activity.EditUserActivity;
import com.zjcql.activity.UserActivity;
import com.zjcql.apparatus.Poper;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.popup.KeyBoardPopup;
import com.zjcql.popup.ShowDetailGoodsPopup;
import com.zjcql.popup.UpAndDwonPopup;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.TimeUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

/** 
* 商品浏览适配器
* @author phlofy
* @date 2015年9月19日 下午12:38:14 
*/
public class UserGoodsAdapter extends BaseContentAdapter<Goods>{
	private int layout;
	public UserGoodsAdapter(Context context, List<Goods> list, int layout) {
		super(context, list);
		this.layout = layout;
	}

	@Override
	public View getConvertView(final int position, View convertView, ViewGroup parent) {
		/*****************************获取convertView**********************************/
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(layout, null);
			viewHolder.icon = (CircleImageView) convertView.findViewById(R.id.icon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.pop = (TextView) convertView.findViewById(R.id.pop);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.money = (TextView) convertView.findViewById(R.id.money);
			viewHolder.state = (TextView) convertView.findViewById(R.id.state);
			viewHolder.user_show_layout = (RelativeLayout) convertView.findViewById(R.id.users_show_layout);
			viewHolder.goods_show_layout = convertView.findViewById(R.id.layout_menu);
			convertView.setTag(viewHolder);// 之前少了这个语句，导致出现空指针异常
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		/*******************************得到商品、用户实体******************************/
		final Goods goods = dataList.get(position);
		User user = goods.getUser();
		
		/*******************************给各个view添加信息******************************/
		MyBitmapUtil.displayIcon(mContext, user, viewHolder.icon);
		viewHolder.name.setText(user.getNick());
		viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(goods.getCreatedAt(), "yyyy-MM-dd HH:mm:ss")));
		Poper.getInstance().getPop(mContext, user, new GeneralListener() {
			
			@Override
			public void onReturn(boolean arg0, int arg1, String arg2) {
				if(arg0){
					viewHolder.pop.setText(arg2);
				}
			}
		});
		if(goods.getFiles() != null)
			MyBitmapUtil.displayImage(mContext, goods.getFiles()[0], viewHolder.image);
		else
			viewHolder.image.setImageResource(R.drawable.no_image);
		
		viewHolder.title.setText(goods.getTitle());
		viewHolder.money.setText(goods.getPrice()+"");
		viewHolder.state.setText(goods.getState());
		
		/**********************************添加监听器**********************************/
		viewHolder.user_show_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", goods.getUser());
				in.putExtras(bundle);
				mContext.startActivity(in);
			}
		});
		
		viewHolder.goods_show_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowDetailGoodsPopup sdgp = new ShowDetailGoodsPopup(mContext, viewHolder.goods_show_layout, goods);
				sdgp.show();
				sdgp.getFloatButton(new GeneralListener() {
					
					@Override
					public void onReturn(boolean arg0, int arg1, String arg2) {
						BmobUser bmobUser = BmobUser.getCurrentUser(mContext);
						if(bmobUser == null){
							toast("还未登录。");
							return;
						}
						User newUser = new User();
						BmobRelation relation = new BmobRelation();
						relation.add(goods);
						newUser.setRelateGoods(relation);
						newUser.update(mContext, bmobUser.getObjectId(), new UpdateListener() {
							@Override
							public void onSuccess() {
								toast("已加入购物车");
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								toast("失败原因："+arg1);
							}
						});
					}
				});
			}
		});
		return convertView;
	}
	
	public static class ViewHolder {
		public CircleImageView icon;
		public TextView name;
		public TextView time;
		public TextView pop;
		public ImageView image;
		public TextView title;
		public TextView money;
		public TextView state;
		public RelativeLayout user_show_layout;
		public View goods_show_layout;
	}
}
