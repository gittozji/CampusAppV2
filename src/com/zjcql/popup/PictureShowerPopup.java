package com.zjcql.popup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zjcql.base.BasePopupWindow;
import com.zjcql.base.MyApplication;
import com.zjcql.campusappv2.R;
import com.zjcql.rebuildview.TouchImageView;
import com.zjcql.util.MyBitmapUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/** 
* 单一图片展示（可缩放）
* @author phlofy
* @date 2015年10月10日 下午8:53:14 
*/
public class PictureShowerPopup extends BasePopupWindow{
	String url;
	TouchImageView tiv;
	boolean isIcon = false;
	public PictureShowerPopup(Context mContext, View mRootViwe, String url) {
		super(mContext, mRootViwe);
		this.url = url;
		doWork();
	}
	public PictureShowerPopup(Context mContext, View mRootViwe, String url,boolean isIcon) {
		super(mContext, mRootViwe);
		this.url = url;
		this.isIcon = isIcon;
		doWork();
	}

	@Override
	public void doWork() {
		mPopupLayout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_layout_picture_shower, null);
		mPopupLayout.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				mPopupWindow.dismiss();
			}
		});
		tiv = (TouchImageView) mPopupLayout.findViewById(R.id.image);
		if(isIcon){
			MyBitmapUtil.displayIcon(mContext, url, tiv);
		}
		else{
			MyBitmapUtil.displayImage(mContext, url, tiv);
		}
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		mPopupWindow.setAnimationStyle(R.style.PictureShowerInOutAnimation);
		// 点击外面区域自动退出Popup
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	@Override
	public void show() {
		mPopupWindow.showAtLocation(mRootView,Gravity.TOP, 0, 0);
	}
	
}
