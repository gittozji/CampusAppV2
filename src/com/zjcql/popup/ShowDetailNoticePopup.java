package com.zjcql.popup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.Interface.GeneralListener;
import com.zjcql.activity.UserActivity;
import com.zjcql.apparatus.Poper;
import com.zjcql.base.BasePopupWindow;
import com.zjcql.base.MyApplication;
import com.zjcql.bmobbean.Notices;
import com.zjcql.campusappv2.R;
import com.zjcql.dialog.SelectColorDialog;
import com.zjcql.util.MyBitmapUtil;
import com.zjcql.util.TimeUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/** 
* 消息详细信息展示popuwindow
* @author phlofy
* @date 2015年10月17日 下午6:05:08 
*/
public class ShowDetailNoticePopup extends BasePopupWindow{
	Notices mNotices;
	ImageView mImage,mIcon;
	TextView mTitle,mContent,mName,mTime,mPop;
	public ShowDetailNoticePopup(Context mContext, View mRootViwe,Notices notices) {
		super(mContext, mRootViwe);
		this.mNotices = notices;
		doWork();
	}

	@Override
	public void doWork() {
		mPopupLayout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_layout_detail_notices, null);
		mPopupLayout.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				mPopupWindow.dismiss();
			}
		});
		// 获得组件
		
		mImage = (ImageView) mPopupLayout.findViewById(R.id.image);
		mIcon = (ImageView) mPopupLayout.findViewById(R.id.icon);
		mTitle = (TextView) mPopupLayout.findViewById(R.id.title);
		mTime = (TextView) mPopupLayout.findViewById(R.id.time);
		mName = (TextView) mPopupLayout.findViewById(R.id.name);
		mPop = (TextView) mPopupLayout.findViewById(R.id.pop);
		mContent = (TextView) mPopupLayout.findViewById(R.id.content);
		
		// 设置信息
		if(mNotices.getFile() != null)
			MyBitmapUtil.displayImage(mContext, mNotices.getFile(), mImage);
		else
			mImage.setImageResource(R.drawable.user_back_small);
		MyBitmapUtil.displayIcon(mContext, mNotices.getUser(), mIcon);
		mTitle.setTextColor(Color.parseColor(SelectColorDialog.colors[mNotices.getIndex()]));
		mTitle.setText(mNotices.getTitle());
		mContent.setText(mNotices.getContent());
		mName.setText(mNotices.getUser().getNick());;
		mTime.setText(TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(mNotices.getCreatedAt(), "yyyy-MM-dd HH:mm:ss")));
		Poper.getInstance().getPop(mContext, mNotices.getUser(), new GeneralListener() {
			
			@Override
			public void onReturn(boolean arg0, int arg1, String arg2) {
				mPop.setText(arg2);
			}
		});
		
		// 设置点击事件
		mImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mNotices.getFile() != null){
					new PictureShowerPopup(mContext, mRootView, mNotices.getFile()).show();
				}
				else{
					new PictureShowerPopup(mContext, mRootView, "null").show();
				}
			}
		});
		mPopupLayout.findViewById(R.id.users_show_layout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(mContext,UserActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", mNotices.getUser());
				in.putExtras(bundle);
				mContext.startActivity(in);
			}
		});
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		mPopupWindow.setAnimationStyle(R.style.ShowDetailGoodsInOutAnimation);
		// 点击外面区域自动退出Popup
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	
}
