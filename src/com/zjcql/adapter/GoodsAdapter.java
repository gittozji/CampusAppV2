package com.zjcql.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.base.BaseActivity;
import com.zjcql.base.BaseContentAdapter;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Goods;
import com.zjcql.bmobbean.User;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.EditDialog;
import com.zjcql.popup.ShowDetailGoodsPopup;
import com.zjcql.popup.UpAndDwonPopup;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;
import com.zjcql.util.TimeUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;

/** 
* 商品浏览Adapter
* @author phlofy
* @date 2015年10月2日 下午3:47:35 
*/
public class GoodsAdapter extends BaseContentAdapter<Goods>{
	public GoodsAdapter(Context context, List<Goods> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		/*******************************得到商品实体******************************/
		final Goods goods = dataList.get(position);
		
		View rootView;
		if(goods.getIsBuy()){
			rootView = mInflater.inflate(R.layout.item_list_buy_no_user, null);
		}
		else{
			rootView = mInflater.inflate(R.layout.item_list_goods_no_user, null);
		}
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.time = (TextView) rootView.findViewById(R.id.time);
		viewHolder.menu = (ImageButton) rootView.findViewById(R.id.menu);
		if(!MyUtil.isEqual(BmobUser.getCurrentUser(mContext, User.class), goods.getUser())){
			viewHolder.menu.setVisibility(View.GONE);
		}
		viewHolder.image = (ImageView) rootView.findViewById(R.id.image);
		viewHolder.title = (TextView) rootView.findViewById(R.id.title);
		viewHolder.money = (TextView) rootView.findViewById(R.id.money);
		viewHolder.state = (TextView) rootView.findViewById(R.id.state);
		viewHolder.goods_show_layout = rootView.findViewById(R.id.layout_menu);

		
		/*******************************给各个view添加信息******************************/
		viewHolder.time.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(goods.getCreatedAt(), "yyyy-MM-dd HH:mm:ss")));
		if(goods.getFiles() != null)
			MyBitmapUtil.displayImage(mContext, goods.getFiles()[0], viewHolder.image);
		else
			viewHolder.image.setImageResource(R.drawable.no_image);
		viewHolder.title.setText(goods.getTitle());
		viewHolder.money.setText(goods.getPrice()+"");
		viewHolder.state.setText(goods.getState());
		
		/**********************************添加监听器**********************************/
		
		viewHolder.goods_show_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(MyUtil.isEqual(BmobUser.getCurrentUser(mContext, User.class), goods.getUser())){
					new ShowDetailGoodsPopup(mContext, viewHolder.goods_show_layout, goods).show();
				}
				else{
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
			}
		});
		
		viewHolder.menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UpAndDwonPopup up = new UpAndDwonPopup(mContext, viewHolder.menu, "删除", "更新");
				up.setGeneralListener(new GeneralListener() {
					
					@Override
					public void onReturn(boolean arg0, int arg1, String arg2) {
						if(arg1 == 1){   // 删除
							new EditDialog(mContext,"是否删除？",false) {
								
								@Override
								public void onClickEventForButton2(String text) {
									final String [] willBeDelete = goods.getFilesName();
									goods.delete(mContext, new DeleteListener() {
										
										@Override
										public void onSuccess() {
											dataList.remove(goods);
											notifyDataSetChanged();
											if(willBeDelete != null){
												for(int i = 0;i < willBeDelete.length;i++){   // 删除文件，无关紧要不做任何成功或错误提示
													BmobFile file = new BmobFile();
													file.setUrl(goods.getFilesName()[i]);
													file.delete(mContext);
												}
											}
										}
										
										@Override
										public void onFailure(int arg0, String arg1) {
											toast(arg1);
										}
									});
								}
								
								@Override
								public void onClickEventForButton1() {
									System.out.println("取消");
								}
							}.show();;
						}
						else{  // 更新
							toast("暂不允许更新，你可以尝试直接删除");
						}
					}
				});
				up.show();
			}
		});
		
		return rootView;
	}
	
	public static class ViewHolder {
		public TextView time;
		public ImageButton menu;
		public ImageView image;
		public TextView title;
		public TextView money;
		public TextView state;
		public View goods_show_layout;
	}
}