package com.zjcql.popup;

import com.zjcql.campusappv2.R;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

/** 
* 一些耗时操作中为了强制让用户等待的popup
* @author phlofy
* @date 2015年9月5日 下午10:40:38 
*/
public class LoadingPopup {
	/**
	 * 上下文
	 */
	Context mContext;
	/**
	 * PopupWindow所依附的根View
	 */
	View mRootView;
	/**
	 * PopupWindow对象，默认大小是覆盖整个屏幕
	 */
	PopupWindow mPopupWindow;
	/**
	 * PopupWindow的界面布局View
	 */
	View mPopupLayout;
	/**
	 * 文字提示
	 */
	TextView mTextView;
	/**
	 * 默认文字提示：正在保存。。。
	 * @param context
	 * @param view
	 */
	public LoadingPopup(Context context, View view){
		this.mContext = context;
		this.mRootView = view;
		mPopupLayout = ((Activity)context).getLayoutInflater().inflate(R.layout.popup_layout_loading, null);
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,false);
	}
	/**
	 * 自定义文字提示
	 * @param context
	 * @param view
	 * @param string
	 */
	public LoadingPopup(Context context, View view ,String string){
		this.mContext = context;
		this.mRootView = view;
		mPopupLayout = ((Activity)context).getLayoutInflater().inflate(R.layout.popup_layout_loading, null);
		mTextView = (TextView) mPopupLayout.findViewById(R.id.text);
		mTextView.setText(string);
		mPopupWindow = new PopupWindow(mPopupLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,false);
	}
	public void show(){
		mPopupWindow.showAtLocation(mRootView,Gravity.BOTTOM, 0, 0);
	}
	public void dismiss(){
		mPopupWindow.dismiss();
	}
}
