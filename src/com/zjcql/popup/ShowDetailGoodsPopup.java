package com.zjcql.popup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.activity.EditUserActivity;
import com.zjcql.activity.UserActivity;
import com.zjcql.base.BasePopupWindow;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Goods;
import com.zjcql.campusappv2.R;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

/** 
* 这是展示商品详细信息的popupwindow
* 今天还不知道该怎么传递商品信息。因此先不写
* @author phlofy
* @date 2015年8月25日 下午4:56:59 
*/
public class ShowDetailGoodsPopup extends BasePopupWindow{
	Goods mGoods;
	TextView title,content,price,state,classify,type;
	GridView grid;
	BaseAdapter adapter;
	ImageButton floatButton;
	public ShowDetailGoodsPopup(Context mContext, View mRootViwe, Goods mGoods) {
		super(mContext, mRootViwe);
		this.mGoods = mGoods;
		doWork();
	}

	@Override
	public void doWork() {
		// 获取控件
		mPopupLayout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_layout_detail_goods, null);
		title = (TextView) mPopupLayout.findViewById(R.id.title);
		content = (TextView) mPopupLayout.findViewById(R.id.content);
		price = (TextView) mPopupLayout.findViewById(R.id.price);
		state = (TextView) mPopupLayout.findViewById(R.id.state);
		classify = (TextView) mPopupLayout.findViewById(R.id.classify);
		type = (TextView) mPopupLayout.findViewById(R.id.type);
		grid = (GridView) mPopupLayout.findViewById(R.id.grid);
		mPopupLayout.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				mPopupWindow.dismiss();
			}
		});
		
		// 设置信息
		title.setText(mGoods.getTitle());
		content.setText(mGoods.getContent());
		price.setText(mGoods.getPrice()+"");
		state.setText(mGoods.getState());
		classify.setText(mGoods.getClassify());
		if(mGoods.getIsBuy()){
			type.setText("求购");
		}
		else{
			type.setText("出售");
		}
		adapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// 获取屏幕的宽度，用于动态控制三个image的大小
				int mScreenWidth = MyUtil.getScreenWidth(mContext) - MyUtil.dip2px(mContext,26);
				AbsListView.LayoutParams mLayoutParams =  new AbsListView.LayoutParams(mScreenWidth/3, mScreenWidth/3);
				ImageView mImageView = new ImageView(mContext);
				mImageView.setScaleType(ScaleType.CENTER_CROP);
				mImageView.setLayoutParams(mLayoutParams);
				MyBitmapUtil.displayImage(mContext,mGoods.getFiles()[position], mImageView);
				return mImageView;
			}
			
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				if(mGoods.getFiles() != null)
					return mGoods.getFiles().length;
				return 0;
			}
		};
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				new PictureShowerPopup(mContext,mRootView,mGoods.getFiles()[position]).show();
			}
		});
		
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		mPopupWindow.setAnimationStyle(R.style.ShowDetailGoodsInOutAnimation);
		// 点击外面区域自动退出Popup
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	
	public void windowDismiss(){
		mPopupWindow.dismiss();
	}
	/**
	 * @param gl 规则是返回的三个参数没有用处
	 */
	public void getFloatButton(final GeneralListener gl){
		floatButton = (ImageButton) mPopupLayout.findViewById(R.id.floatbutton);
		floatButton.setVisibility(View.VISIBLE);
		floatButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gl.onReturn(true, 0, null);
			}
		});
	}
	/**
	 * 
	 * @param gl 规则是按钮1被点击返回（true，1，null），按钮2被点击返回（true，2，null）
	 */
	public void getTwoButton(final GeneralListener gl){
		mPopupLayout.findViewById(R.id.lay_bottom).setVisibility(View.VISIBLE);
		mPopupLayout.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gl.onReturn(true, 1, null);
			}
		});
		mPopupLayout.findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gl.onReturn(true, 2, null);
			}
		});
	}
}