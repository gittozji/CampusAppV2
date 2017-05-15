package com.zjcql.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.activity.UserActivity;
import com.zjcql.adapter.GoodsAdapter.ViewHolder;
import com.zjcql.apparatus.Poper;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.dialog.TwoSelectDialog;
import com.zjcql.popup.ShowDetailGoodsPopup;
import com.zjcql.popup.UpAndDwonPopup;
import com.zjcql.rebuildview.CircleImageView;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.TimeUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;

/** 
* 购物车Adapter
* @author phlofy
* @date 2015年10月5日 下午3:30:01 
*/
public class CartAdapter extends BaseContentAdapter<Goods>{
	public CartAdapter(Context context,List<Goods> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*******************************得到商品实体******************************/
		final Goods goods = dataList.get(position);
		User user = goods.getUser();
		
		/*******************************获取View********************************/
		View rootView;
		if(goods.getIsBuy() == null){
			System.out.println(position+"wei");
			System.out.println(goods.getTitle());
		}
		if(goods.getIsBuy()){
			rootView = mInflater.inflate(R.layout.item_list_buy, null);
		}
		else{
			rootView = mInflater.inflate(R.layout.item_list_goods, null);
		}
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.icon = (CircleImageView) rootView.findViewById(R.id.icon);
		viewHolder.name = (TextView) rootView.findViewById(R.id.name);
		viewHolder.time = (TextView) rootView.findViewById(R.id.time);
		viewHolder.pop = (TextView) rootView.findViewById(R.id.pop);
		viewHolder.image = (ImageView) rootView.findViewById(R.id.image);
		viewHolder.title = (TextView) rootView.findViewById(R.id.title);
		viewHolder.money = (TextView) rootView.findViewById(R.id.money);
		viewHolder.state = (TextView) rootView.findViewById(R.id.state);
		viewHolder.user_show_layout = (RelativeLayout) rootView.findViewById(R.id.users_show_layout);
		viewHolder.goods_show_layout = rootView.findViewById(R.id.layout_menu);

		
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
				final ShowDetailGoodsPopup sp = new ShowDetailGoodsPopup(mContext, viewHolder.goods_show_layout, goods);
				sp.show();
				sp.getTwoButton(new GeneralListener() {
					
					@Override
					public void onReturn(boolean arg0, int arg1, String arg2) {
						switch(arg1){
						case 1:{
							final User u = new User();
							BmobRelation r = new BmobRelation();
							r.remove(goods);
							u.setRelateGoods(r);
							new EditDialog(mContext,"是否删除？",false) {
								
								@Override
								public void onClickEventForButton2(String text) {
									u.update(mContext, BmobUser.getCurrentUser(mContext, User.class).getObjectId(), new UpdateListener() {
										@Override
										public void onSuccess() {
											dataList.remove(goods);
											notifyDataSetChanged();
											sp.windowDismiss();
										}
										
										@Override
										public void onFailure(int arg0, String arg1) {
											toast("删除失败:"+arg1);
										}
									});
								}

								@Override
								public void onClickEventForButton1() {
									// TODO Auto-generated method stub
									
								}
							}.show();
							break;
						}
						case 2:{
							Intent in = new Intent(mContext,UserActivity.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("user", goods.getUser());
							in.putExtras(bundle);
							mContext.startActivity(in);
							break;
						}
						}
					}
				});
			}
		});
		return rootView;
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
